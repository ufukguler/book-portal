package com.bookportal.api.service;

import com.bookportal.api.configs.EnvironmentVariables;
import com.bookportal.api.entity.PasswordReset;
import com.bookportal.api.entity.User;
import com.bookportal.api.repository.PasswordResetRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PasswordResetService.class)
class PasswordResetServiceTest {

    @MockBean
    EnvironmentVariables env;

    @MockBean
    PasswordResetRepository passwordResetRepository;

    @Autowired
    PasswordResetService passwordResetService;

    @Test
    void generatePasswordResetKey() {
        PasswordReset passwordReset = new PasswordReset();

        Mockito.lenient()
                .when(passwordResetRepository.save(passwordReset))
                .thenReturn(passwordReset);

        String returnedKey = passwordResetService.generatePasswordResetKey(new User());
        assertNotNull(returnedKey);
    }

    @Test
    void isValidKey() {
        User user = new User();
        user.setMail("ufuk@mail.co");
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUser(user);
        passwordReset.setValidity(new Date(System.currentTimeMillis() + PasswordResetService.DAY));
        Mockito.lenient()
                .when(passwordResetRepository.findBySecretKeyAndActiveTrue("key"))
                .thenReturn(Optional.of(passwordReset));
        boolean key = passwordResetService.isValidKey("key", user.getMail());
        assertTrue(key);
    }

    @Test
    void isValidKey_exception_ResponseStatusException() {
        User user = new User();
        user.setMail("ufuk@mail.co");
        Mockito.lenient()
                .when(passwordResetRepository.findBySecretKeyAndActiveTrue("key"))
                .thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> passwordResetService.isValidKey("key",user.getMail()));
    }

    @Test
    void updateUserKey() {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setValidity(new Date(System.currentTimeMillis() + PasswordResetService.DAY));
        Mockito.lenient()
                .when(passwordResetRepository.findBySecretKeyAndActiveTrue("key"))
                .thenReturn(Optional.of(passwordReset));
        Mockito.lenient()
                .when(passwordResetRepository.save(passwordReset))
                .thenReturn(passwordReset);
        boolean key = passwordResetService.updateUserKey("key");
        assertTrue(key);
    }

    @Test
    void updateUserKey_throw_ResponseStatusException() {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setValidity(new Date(System.currentTimeMillis() + PasswordResetService.DAY));
        Mockito.lenient()
                .when(passwordResetRepository.findBySecretKeyAndActiveTrue("key"))
                .thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> passwordResetService.updateUserKey("key"));
    }
}