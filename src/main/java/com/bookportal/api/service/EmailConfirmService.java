package com.bookportal.api.service;

import com.bookportal.api.entity.EmailConfirm;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.EmailConfirmRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailConfirmService {
    private final EmailConfirmRepository emailConfirmRepository;

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public EmailConfirm generateEmailConfirmationKey(User user) {
        EmailConfirm emailConfirm = new EmailConfirm();
        emailConfirm.setUser(user);
        emailConfirm.setSecretKey(generateRandomKey());
        return emailConfirmRepository.save(emailConfirm);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public boolean updateUserKeyToInactive(String key) {
        Optional<EmailConfirm> byKey = emailConfirmRepository.findBySecretKeyAndActiveTrue(key);
        if (byKey.isPresent()) {
            EmailConfirm emailConfirm = byKey.get();
            emailConfirm.setActive(false);
            emailConfirmRepository.save(emailConfirm);
            return true;
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.KEY.getValue());
    }

    public EmailConfirm findBySecretKeyAndActiveTrue(String key) {
        Optional<EmailConfirm> byKey = emailConfirmRepository.findBySecretKeyAndActiveTrue(key);
        if (byKey.isPresent()) {
            return byKey.get();
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.KEY.getValue());
    }

    private String generateRandomKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
