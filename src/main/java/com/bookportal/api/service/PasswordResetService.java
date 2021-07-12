package com.bookportal.api.service;

import com.bookportal.api.configs.EnvironmentVariables;
import com.bookportal.api.entity.PasswordReset;
import com.bookportal.api.entity.User;
import com.bookportal.api.repository.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    public final static Long DAY = 1000L * 60 * 60 * 24;
    private final PasswordResetRepository passwordResetRepository;
    private final EnvironmentVariables env;

    public String generatePasswordResetKey(User user) {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUser(user);
        passwordReset.setSecretKey(generateRandomKey());
        passwordReset.setValidity(new Date(System.currentTimeMillis() + DAY));
        passwordResetRepository.save(passwordReset);
        return passwordReset.getSecretKey();
    }

    public boolean isValidKey(String key, String mail) {
        Optional<PasswordReset> bySecretKey = passwordResetRepository.findBySecretKeyAndActiveTrue(key);
        if (bySecretKey.isPresent() && mail.equals(bySecretKey.get().getUser().getMail())) {
            if (new Date().before(bySecretKey.get().getValidity())) {
                return true;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, env.validityExpired());
    }

    private String generateRandomKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean updateUserKey(String key) {
        Optional<PasswordReset> bySecretKey = passwordResetRepository.findBySecretKeyAndActiveTrue(key);
        if (!bySecretKey.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Key not found!");
        }
        PasswordReset passwordReset = bySecretKey.get();
        passwordReset.setActive(false);
        passwordResetRepository.save(passwordReset);
        return true;
    }
}
