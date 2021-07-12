package com.bookportal.api.service;

import com.bookportal.api.entity.EmailConfirm;
import com.bookportal.api.entity.User;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.EmailConfirmRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = EmailConfirmService.class)
class EmailConfirmServiceTest {

    @MockBean
    EmailConfirmRepository emailConfirmRepository;

    @Autowired
    EmailConfirmService emailConfirmService;

    @Test
    void generateEmailConfirmationKey() {
        EmailConfirm emailConfirm = new EmailConfirm();

        Mockito.lenient()
                .when(emailConfirmRepository.save(any(EmailConfirm.class)))
                .thenReturn(emailConfirm);

        EmailConfirm saved = emailConfirmService.generateEmailConfirmationKey(new User());
        assertNotNull(saved);
        Mockito.verify(emailConfirmRepository, times(1))
                .save(any());
    }

    @Test
    void updateUserKeyToInactive() {
        EmailConfirm emailConfirm = new EmailConfirm();
        emailConfirm.setSecretKey("secret-keey");

        Mockito.lenient()
                .when(emailConfirmRepository.findBySecretKeyAndActiveTrue("secret-keey"))
                .thenReturn(Optional.of(emailConfirm));

        boolean bool = emailConfirmService.updateUserKeyToInactive("secret-keey");
        assertTrue(bool);
    }

    @Test
    void updateUserKeyToInactive_exception() {
        Mockito.lenient()
                .when(emailConfirmRepository.findBySecretKeyAndActiveTrue(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class,
                () -> emailConfirmService.updateUserKeyToInactive("secret-keey"));
    }

    @Test
    void findBySecretKeyAndActiveTrue() {
        EmailConfirm emailConfirm = new EmailConfirm();
        emailConfirm.setSecretKey("secret-keey");
        Mockito.lenient()
                .when(emailConfirmRepository.findBySecretKeyAndActiveTrue(anyString()))
                .thenReturn(Optional.of(emailConfirm));

        EmailConfirm byKey = emailConfirmService.findBySecretKeyAndActiveTrue("secret-keey");
        assertEquals(emailConfirm.getSecretKey(), byKey.getSecretKey());
    }

    @Test
    void findBySecretKeyAndActiveTrue_exception() {
        Mockito.lenient()
                .when(emailConfirmRepository.findBySecretKeyAndActiveTrue(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class,
                () -> emailConfirmService.findBySecretKeyAndActiveTrue("secret"));
    }
}