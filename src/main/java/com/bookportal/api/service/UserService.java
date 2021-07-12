package com.bookportal.api.service;

import com.bookportal.api.configs.EnvironmentVariables;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.model.enums.SocialTypeEnum;
import com.bookportal.api.model.enums.UserRoleEnum;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.RoleRepository;
import com.bookportal.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EnvironmentVariables env;
    private final EmailService emailService;

    public Optional<User> findByJustMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public Optional<User> findByIdAndActiveTrue(Long id) {
        return userRepository.findByIdAndActiveTrue(id);
    }

    public Optional<User> findByMail(String mail) {
        return userRepository.findByMailAndActiveTrue(mail);
    }

    public Optional<User> findBySocialId(String socialId) {
        return userRepository.findByGoogleIdOrFacebookId(socialId, socialId);
    }

    public Optional<User> findByMailAndActiveFalse(String mail) {
        return userRepository.findByMailAndActiveFalse(mail);
    }

    public Optional<User> getCurrentUser() {
        return userRepository.findByMailAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public boolean updatePassword(String mail, String password) {
        Optional<User> byMail = userRepository.findByMailAndActiveTrue(mail);
        if (byMail.isPresent()) {
            User user = byMail.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return true;
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.USER.getValue());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User createUser(User user) {
        isMailExist(user.getMail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setSocial(false);
        user.setRoles(Collections.singletonList(roleRepository.findByName(UserRoleEnum.ROLE_USER.name())));
        User savedUser = userRepository.save(user);
        User userToInactive = setUserToInactive(savedUser.getId());
        sendconfirmationEmail(userToInactive);
        return userToInactive;
    }

    private void sendconfirmationEmail(User user) {
        emailService.sendEmailConfirmationLink(user);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User setUserToInactive(Long id) {
        Optional<User> user = userRepository.findByIdAndActiveTrue(id);
        if (user.isPresent()) {
            userRepository.setToInactive(id);
            user.get().setActive(false); // not updating because of the transaction.. so hardcoded
            return user.get();
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.USER.getValue());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User setUserToActive(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.setToActive(id);
            user.get().setActive(true); // not updating because of the transaction.. so hardcoded
            return user.get();
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.USER.getValue());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User saveGuest(User user) {
        Optional<User> byMail = userRepository.findByMail(user.getMail());
        if (byMail.isPresent()) {
            return byMail.get();
        }
        user.setPassword(passwordEncoder.encode(generateRandomPassword()));
        user.setRoles(Collections.singletonList(roleRepository.findByName(UserRoleEnum.ROLE_GUEST.name())));
        return userRepository.save(user);
    }

    private void isMailExist(String mail) {
        if (userRepository.findByMail(mail).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, env.eMailAlreadyInUse());
        }
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User createSocialUser(User user) {
        isMailExist(user.getMail());
        isFacebookIdExist(user.getFacebookId());
        isGoogleIdExist(user.getGoogleId());
        user.setPassword(passwordEncoder.encode(generateRandomPassword()));
        user.setRoles(Collections.singletonList(roleRepository.findByName(UserRoleEnum.ROLE_USER.name())));
        setSocialId(user);
        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User updateSocialId(User user, SocialTypeEnum typeEnum, String id) {
        if (typeEnum.equals(SocialTypeEnum.FACEBOOK)) {
            isFacebookIdExist(user.getFacebookId());
            user.setFacebookId(id);
        } else {
            isGoogleIdExist(user.getGoogleId());
            user.setGoogleId(id);
        }
        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User updateUserMail(User user, String mail) {
        user.setMail(mail);
        return userRepository.save(user);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

    private void setSocialId(User user) {
        if (user.getGoogleId() != null && user.getSocialType().equals(SocialTypeEnum.GOOGLE.getValue())) {
            user.setGoogleId(user.getGoogleId());
        } else if (user.getFacebookId() != null && user.getSocialType().equals(SocialTypeEnum.FACEBOOK.getValue())) {
            user.setFacebookId(user.getFacebookId());
        }
    }

    private boolean isFacebookIdExist(String facebookId) {
        if (facebookId != null && userRepository.findByFacebookId(facebookId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, env.facebookIdAlreadyInUse());
        }
        return false;
    }

    private boolean isGoogleIdExist(String googleId) {
        if (googleId != null && userRepository.findByGoogleId(googleId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, env.googleIdAlreadyInUse());
        }
        return false;
    }
}
