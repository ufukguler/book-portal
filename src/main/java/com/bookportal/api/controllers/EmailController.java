package com.bookportal.api.controllers;

import com.bookportal.api.configs.EnvironmentVariables;
import com.bookportal.api.entity.EmailConfirm;
import com.bookportal.api.entity.User;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.model.PasswordResetDTO;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.service.EmailConfirmService;
import com.bookportal.api.service.EmailService;
import com.bookportal.api.service.PasswordResetService;
import com.bookportal.api.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/sendMail")
@RequiredArgsConstructor
public class EmailController {
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;
    private final EmailConfirmService emailConfirmService;
    private final EnvironmentVariables env;

    @GetMapping("/resetPassword")
    @ApiOperation(value = "Send password reset mail")
    public ResponseEntity<?> resetPassword(
            @ApiParam(value = "User e-mail", defaultValue = "test@appmedia.com", required = true)
            @RequestParam("email") String email) {
        Optional<User> byMail = userService.findByMail(email);
        if (byMail.isPresent()) {
            String nameSurname = byMail.get().getName() + " " + byMail.get().getSurname();
            String key = passwordResetService.generatePasswordResetKey(byMail.get());
            new Thread(() -> emailService.sendPasswordResetLink(nameSurname, email, key)).start();
            return ResponseEntity.ok(true);
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.USER.getValue());
    }

    @PostMapping("/resetPassword")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Reset password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordResetDTO dto) {
        Optional<User> byMail = userService.findByMail(dto.getEmail());
        if (byMail.isPresent()) {
            if (passwordResetService.isValidKey(dto.getKey(), byMail.get().getMail())) {
                userService.updatePassword(dto.getEmail(), dto.getNewPass());
                passwordResetService.updateUserKey(dto.getKey());
                return ResponseEntity.ok(true);
            }
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.USER.getValue());
    }

    @GetMapping("/confirmEmail")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Confirm mail address")
    public ResponseEntity<?> confirmEmail(
            @ApiParam(value = "User e-mail address", defaultValue = "test@appmedia.com", required = true) @RequestParam("email") String email,
            @ApiParam(value = "Token", defaultValue = "c2188bc5d8df4dfb8c66a2e593043ace", required = true) @RequestParam("key") String key){
        Optional<User> byMail = userService.findByMailAndActiveFalse(email);
        if (byMail.isPresent()) {
            EmailConfirm bySecret = emailConfirmService.findBySecretKeyAndActiveTrue(key);// throws if not exist
            emailConfirmService.updateUserKeyToInactive(key);
            userService.setUserToActive(bySecret.getUser().getId());
            return ResponseEntity.ok(true);
        } else if (userService.findByMail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, env.userAlreadyConfirmedEmail());
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.USER.getValue());
    }
}
