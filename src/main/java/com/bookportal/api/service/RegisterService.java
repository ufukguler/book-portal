package com.bookportal.api.service;

import com.bookportal.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserService userService;

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User createUser(User user) {
        return userService.createUser(user);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public User saveGuest(User user) {
        return userService.saveGuest(user);
    }


}
