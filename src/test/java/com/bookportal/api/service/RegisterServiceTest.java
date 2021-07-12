package com.bookportal.api.service;

import com.bookportal.api.entity.User;
import com.bookportal.api.model.enums.SocialTypeEnum;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RegisterService.class)
class RegisterServiceTest {

    @MockBean
    UserService userService;

    @Autowired
    RegisterService registerService;

    @Test
    void createUser() {
        User user = initSocialUser();
        Mockito.lenient()
                .when(userService.createUser(user))
                .thenReturn(user);
        User savedUser = registerService.createUser(user);
        assertNotNull(savedUser);
        assertEquals(user.getMail(), savedUser.getMail());

    }

    @Test
    void saveGuest() {
        User guest = initGuestUser();
        Mockito.lenient()
                .when(userService.saveGuest(guest))
                .thenReturn(guest);
        User guestUser = registerService.saveGuest(guest);
        assertNotNull(guestUser);
    }

    private User initSocialUser() {
        User user = new User();
        user.setMail("ufukglr@yandex.com");
        user.setName("ufuk");
        user.setSurname("guler");
        user.setSocialType(SocialTypeEnum.FACEBOOK.getValue());
        user.setFacebookId("12345");
        user.setPpUrl("test12345");
        return user;
    }

    private User initUser() {
        User user = new User();
        user.setMail("ufukglr@yandex.com");
        user.setName("ufuk");
        user.setSurname("guler");
        return user;
    }

    private User initGuestUser() {
        User user = new User();
        user.setMail("androidId");
        return user;
    }

}