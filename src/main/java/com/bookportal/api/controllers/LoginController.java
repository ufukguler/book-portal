package com.bookportal.api.controllers;

import com.bookportal.api.configs.EnvironmentVariables;
import com.bookportal.api.entity.User;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.model.enums.SocialTypeEnum;
import com.bookportal.api.service.RegisterService;
import com.bookportal.api.service.UserService;
import com.bookportal.api.service.auth.JwtUserDetailsService;
import com.bookportal.api.util.mapper.UserMapper;
import com.bookportal.api.auth.JwtTokenUtil;
import com.bookportal.api.model.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final RegisterService registerService;
    private final EnvironmentVariables env;
    private final UserService userService;

    @PostMapping
    @ApiOperation(value = "Login mail&pass")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody UserDTO userDTO) {
        authenticateUser(userDTO.getMail(), userDTO.getPassword());
        Optional<User> byJusMail = userService.findByJustMail(userDTO.getMail());
        if (byJusMail.isPresent() && byJusMail.get().isActive()) {
            return setJwtResponse(userDTO.getMail());
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, env.userInactive());
    }

    @PostMapping("/social")
    @ApiOperation(value = "Login social account")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ResponseEntity<?> createSocialAuthenticationToken(@Valid @RequestBody SocialDTO dto) {
        isSocialTypeExist(dto);
        boolean isGoogle;
        String socialId;
        if (dto.getSocialType().equals(SocialTypeEnum.FACEBOOK.getValue())) {
            if (dto.getFacebookId().length() == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Facebook ID değeri geçersiz!");
            }
            isGoogle = false;
            socialId = dto.getFacebookId();
        } else {
            if (dto.getGoogleId().length() == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google ID değeri geçersiz!");
            }
            isGoogle = true;
            socialId = dto.getGoogleId();
        }
        dto.setPpUrl(dto.getPpUrl().trim());
        if (dto.getPpUrl().isEmpty() || dto.getPpUrl().trim().length() == 0) {
            dto.setPpUrl(null);
        }
        return getJwtResponseDTOResponse(dto, isGoogle, socialId);
    }

    @PostMapping("/refresh")
    @ApiOperation(value = "Refresh token by mail & old token")
    public ResponseEntity<?> refreshAuthenticationToken(@Valid @RequestBody JwtRefreshDTO jwtRefreshDTO) {
        final String username = jwtTokenUtil.getUsernameFromToken(jwtRefreshDTO.getToken());
        if (!username.equals(jwtRefreshDTO.getMail())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, env.tokenUserNotValid());
        }
        return setJwtResponse(username);
    }

    @PostMapping("/guest")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Guest user")
    public ResponseEntity<?> createGuestToken(@Valid @RequestBody GuestDTO guestDTO) {
        User user = new User();
        user.setMail(guestDTO.getAndroidID());
        registerService.saveGuest(user);
        return setJwtResponse(guestDTO.getAndroidID());
    }

    private void authenticateUser(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, env.userInactive());
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, env.incorrectCredentials());
        }
    }

    public ResponseEntity<JwtResponseDTO> setJwtResponse(String mail) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");
        Optional<User> byMail = userService.findByMail(mail);
        if (byMail.isPresent()) {
            User user = byMail.get();
            final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getMail());
            final String token = jwtTokenUtil.generateToken(userDetails);
            final String username = jwtTokenUtil.getUsernameFromToken(token);
            final String issuedAt = simpleDateFormat.format(new Date());
            final String expireAt = simpleDateFormat.format(jwtTokenUtil.getExpirationDateFromToken(token));
            return ResponseEntity.ok(getJwtResponseDTO(token, username, issuedAt, expireAt, user));
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User could not authenticated");
    }

    private ResponseEntity<JwtResponseDTO> getJwtResponseDTOResponse(SocialDTO dto, boolean isGoogle, String socialId) {
        Optional<User> bySocialId = userService.findBySocialId(socialId);
        Optional<User> byMail = userService.findByMail(dto.getMail());
        if (bySocialId.isPresent() && bySocialId.get().getMail().equals(dto.getMail())) {
            return setJwtResponse(dto.getMail());
        } else if (!byMail.isPresent() && !bySocialId.isPresent()) {
            User user = UserMapper.socialToUser(dto);
            if (isGoogle) {
                user.setFacebookId(null);
            } else {
                user.setGoogleId(null);
            }
            User newSocialUser = userService.createSocialUser(user);
            return setJwtResponse(newSocialUser.getMail());
        } else if (!byMail.isPresent()) {
            User user = bySocialId.get();
            User updatedUser = userService.updateUserMail(user, dto.getMail());
            return setJwtResponse(updatedUser.getMail());
        } else {
            User user = byMail.get();
            SocialTypeEnum socialType = SocialTypeEnum.findByValue(dto.getSocialType());
            User updatedUser = userService.updateSocialId(user, socialType, socialId);
            return setJwtResponse(updatedUser.getMail());
        }
    }

    private void isSocialTypeExist(SocialDTO dto) {
        if (!SocialTypeEnum.isExist(dto.getSocialType())) {
            throw new CustomNotFoundException(ExceptionItemsEnum.SOCIAL_TYPE.getValue());
        }
    }

    private JwtResponseDTO getJwtResponseDTO(String token, String username, String issuedAt, String expireAt, User user) {
        JwtResponseDTO dto = new JwtResponseDTO();
        dto.setStatus(HttpStatus.OK.value());
        dto.setUsername(username);
        dto.setPpUrl(user.getPpUrl());
        dto.setIssuedAt(issuedAt);
        dto.setExpireAt(expireAt);
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setToken(token);
        dto.setUserId(user.getId());
        return dto;
    }
}
