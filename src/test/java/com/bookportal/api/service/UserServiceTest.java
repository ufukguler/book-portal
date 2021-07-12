package com.bookportal.api.service;

import com.bookportal.api.configs.EnvironmentVariables;
import com.bookportal.api.entity.Role;
import com.bookportal.api.entity.User;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.model.enums.SocialTypeEnum;
import com.bookportal.api.model.enums.UserRoleEnum;
import com.bookportal.api.repository.RoleRepository;
import com.bookportal.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = UserService.class)
class UserServiceTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    RoleRepository roleRepository;

    @MockBean
    EnvironmentVariables env;

    @MockBean
    EmailService emailService;

    @Autowired
    UserService userService;


    @Mock
    private Authentication auth;

    @Test
    void findByJustMail() {
        Mockito.lenient()
                .when(userRepository.findByMail("mail.com"))
                .thenReturn(Optional.of(new User()));
        Optional<User> byMail = userService.findByJustMail("mail.com");
        assertTrue(byMail.isPresent());
    }

    @Test
    void findByJustMail_notPresent() {
        Mockito.lenient()
                .when(userRepository.findByMail("mail.com"))
                .thenReturn(Optional.empty());
        Optional<User> byMail = userService.findByJustMail("mail.com");
        assertFalse(byMail.isPresent());
    }

    @Test
    void findByJustMail_notPresents() {
        Mockito.lenient()
                .when(userRepository.findByMail("mail.com"))
                .thenReturn(Optional.empty());
        Optional<User> byMail = userService.findByMail("mail.com");
        assertFalse(byMail.isPresent());
    }

    @Test
    void findByIdAndActiveTrue() {
        User user = initUser();

        Mockito.lenient()
                .when(userRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(user));

        Optional<User> byIdA = userService.findByIdAndActiveTrue(1L);

        assertTrue(byIdA.isPresent());
        assertEquals(user.getId(), byIdA.get().getId());
    }

    @Test
    void findBySocialId() {
        User user = initUser();
        user.setSocial(true);
        user.setSocialType("1");
        user.setFacebookId("123213");
        Mockito.lenient()
                .when(userRepository.findByGoogleIdOrFacebookId(user.getFacebookId(), user.getFacebookId()))
                .thenReturn(Optional.of(user));

        Optional<User> byIdA = userService.findBySocialId(user.getFacebookId());

        assertTrue(byIdA.isPresent());
        assertEquals(user.getId(), byIdA.get().getId());
    }

    @Test
    void findByMail() {
        User user = initUser();

        Mockito.lenient()
                .when(userRepository.findByMailAndActiveTrue(anyString()))
                .thenReturn(Optional.of(user));

        Optional<User> byIdA = userService.findByMail("some@mail.co");

        assertTrue(byIdA.isPresent());
        assertEquals(user.getMail(), byIdA.get().getMail());
    }

    @Test
    void getCurrentUser() {
        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.lenient()
                .when(auth.getName()).thenReturn("user@appmedia.com");
        Mockito.lenient()
                .when(userRepository.findByMailAndActiveTrue(anyString()))
                .thenReturn(Optional.of(new User()));

        Optional<User> currentUser = userService.getCurrentUser();
        assertTrue(currentUser.isPresent());
    }

    @Test
    void updatePassword() {
        User user = new User();
        user.setPassword("test123");
        user.setActive(true);

        Mockito.lenient()
                .when(userRepository.findByMailAndActiveTrue(anyString()))
                .thenReturn(Optional.of(user));
        Mockito.lenient()
                .when(userRepository.save(user))
                .thenReturn(user);

        boolean result = userService.updatePassword("mail", "newPass");

        assertTrue(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updatePassword_CustomNotFoundException() {
        Mockito.lenient()
                .when(userRepository.findByMailAndActiveTrue(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class, () -> userService.updatePassword("mail", "newPass"));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void createUser() {
        User initUser = initUser();

        Mockito.lenient()
                .when(userRepository.findByMail(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(userRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(initUser));
        Mockito.lenient()
                .when(roleRepository.findByName(anyString()))
                .thenReturn(new Role());
        Mockito.lenient()
                .when(userRepository.save(initUser))
                .thenReturn(initUser);

        User user = userService.createUser(initUser);

        assertEquals(user.getMail(), initUser.getMail());
        assertEquals(user.getName(), initUser.getName());
        assertEquals(user.getSurname(), initUser.getSurname());
        assertEquals(user.isSocial(), initUser.isSocial());
        assertFalse(user.isActive());
        assertTrue(user.getRoles().size() > 0);
    }

    @Test
    void createUser_mailExist() {
        User initUser = initUser();

        Mockito.lenient()
                .when(userRepository.findByMail(anyString()))
                .thenReturn(Optional.of(new User()));

        assertThrows(ResponseStatusException.class, () -> userService.createUser(initUser));
    }

    @Test
    void createSocialUser_facebook_socialIdExist_ResponseStatusException() {
        User user = new User();
        user.setSocialType(SocialTypeEnum.FACEBOOK.getValue());
        user.setFacebookId("dsf");
        Mockito.lenient()
                .when(userRepository.findByFacebookId(anyString()))
                .thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> userService.createSocialUser(user));
    }

    @Test
    void createSocialUser_google_socialIdExist_ResponseStatusException() {
        User user = new User();
        user.setSocialType(SocialTypeEnum.GOOGLE.getValue());
        user.setGoogleId("dsf");
        Mockito.lenient()
                .when(userRepository.findByGoogleId(anyString()))
                .thenReturn(Optional.of(user));
        assertThrows(ResponseStatusException.class, () -> userService.createSocialUser(user));
    }


    @Test
    void createSocialUser_newGoogleUser() {
        Role role = new Role();
        role.setName(UserRoleEnum.ROLE_USER.name());
        User googleUser = initGoogleUser();

        Mockito.lenient()
                .when(userService.findByMail(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(userRepository.findByFacebookId(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(userRepository.findByGoogleId(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(roleRepository.findByName(anyString()))
                .thenReturn(role);
        Mockito.lenient()
                .when(userRepository.save(googleUser))
                .thenReturn(googleUser);
        User socialUser = userService.createSocialUser(googleUser);

        assertEquals(googleUser.getMail(), socialUser.getMail());
        assertEquals(googleUser.getName(), socialUser.getName());
        assertEquals(googleUser.getSurname(), socialUser.getSurname());
        assertEquals(googleUser.getSocialType(), socialUser.getSocialType());
        assertEquals(googleUser.getPpUrl(), socialUser.getPpUrl());
        assertEquals(googleUser.getGoogleId(), socialUser.getGoogleId());
        assertTrue(socialUser.isSocial());
        assertNull(socialUser.getFacebookId());

    }

    @Test
    void createSocialUser_newFacebookUser() {
        User facebookUser = initFacebookUser();
        Role role = new Role();
        role.setName(UserRoleEnum.ROLE_USER.name());

        Mockito.lenient()
                .when(userService.findByMail(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(userRepository.findByFacebookId(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(userRepository.findByGoogleId(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(roleRepository.findByName(anyString()))
                .thenReturn(role);
        Mockito.lenient()
                .when(userRepository.save(facebookUser))
                .thenReturn(facebookUser);

        User socialUser = userService.createSocialUser(facebookUser);

        assertEquals(facebookUser.getMail(), socialUser.getMail());
        assertEquals(facebookUser.getName(), socialUser.getName());
        assertEquals(facebookUser.getSurname(), socialUser.getSurname());
        assertEquals(facebookUser.getSocialType(), socialUser.getSocialType());
        assertEquals(facebookUser.getPpUrl(), socialUser.getPpUrl());
        assertEquals(facebookUser.getFacebookId(), socialUser.getFacebookId());
        assertTrue(socialUser.isSocial());
        assertNull(socialUser.getGoogleId());


    }


    @Test
    void saveGuest() {
        User guestUser = new User();
        guestUser.setMail("asda-232df-3434");

        Mockito.lenient()
                .when(roleRepository.findByName(anyString()))
                .thenReturn(new Role());
        Mockito.lenient()
                .when(userRepository.save(guestUser))
                .thenReturn(guestUser);
        Mockito.lenient()
                .when(passwordEncoder.encode(anyString()))
                .thenReturn("so-secret-encoded-password");

        User user = userService.saveGuest(guestUser);

        assertEquals(guestUser.getMail(), user.getMail());
        assertFalse(user.getRoles().isEmpty());
        assertNotNull(user.getPassword());

    }

    @Test
    void saveGuest_exist() {
        User guestUser = new User();
        guestUser.setMail("asda-232df-3434");

        Mockito.lenient()
                .when(userRepository.findByMail(guestUser.getMail()))
                .thenReturn(Optional.of(guestUser));

        User savedUser = userService.saveGuest(guestUser);

        assertEquals(guestUser.getMail(), savedUser.getMail());

    }

    private User initUser() {
        User user = new User();
        user.setId(1L);
        user.setMail("test@mail.co");
        user.setName("ad");
        user.setSurname("soyad");
        user.setPassword(new BCryptPasswordEncoder().encode("somepass"));
        user.setRoles(Collections.singletonList(new Role()));
        user.setActive(true);
        return user;
    }

    User initGoogleUser() {
        User user = new User();
        user.setName("name");
        user.setSurname("surname");
        user.setSocial(true);
        user.setSocialType(SocialTypeEnum.GOOGLE.getValue());
        user.setMail("mail@mail.co");
        user.setGoogleId("987654321");
        return user;
    }

    User initFacebookUser() {
        User user = new User();
        user.setName("name");
        user.setSurname("surname");
        user.setSocial(true);
        user.setSocialType(SocialTypeEnum.FACEBOOK.getValue());
        user.setMail("mail@mail.co");
        user.setFacebookId("123456789");
        return user;
    }

    @Test
    void findByMailAndActiveFalse() {
        User user = initUser();
        user.setActive(false);
        Mockito.lenient()
                .when(userRepository.findByMailAndActiveFalse(anyString()))
                .thenReturn(Optional.of(user));
        Optional<User> byMailInactive = userService.findByMailAndActiveFalse("mail");
        assertNotNull(byMailInactive.orElse(null));
    }

    @Test
    void setUserToInactive_exception() {
        Mockito.lenient()
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> userService.setUserToInactive(1L));
    }

    @Test
    void setUserToActive() {
        User user = initUser();
        user.setActive(true);
        Mockito.lenient()
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User activeUser = userService.setUserToActive(3L);
        assertTrue(activeUser.isActive());
    }

    @Test
    void setUserToActive_exception() {
        Mockito.lenient()
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> userService.setUserToActive(1L));
    }

    @Test
    void updateSocialId_facebookId() {
        String id = "12345";
        User user = new User();
        user.setSocialType("1");
        Mockito.lenient()
                .when(userRepository.findByFacebookId(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(userRepository.save(user))
                .thenReturn(user);
        User updated = userService.updateSocialId(user, SocialTypeEnum.FACEBOOK, id);
        assertEquals(id, updated.getFacebookId());
    }

    @Test
    void updateSocialId_facebookId_existError() {
        String id = "12345";
        User user = new User();
        user.setSocialType("1");
        user.setFacebookId(id);
        Mockito.lenient()
                .when(userRepository.findByFacebookId(id))
                .thenReturn(Optional.of(user));
        assertThrows(ResponseStatusException.class,
                () -> userService.updateSocialId(user, SocialTypeEnum.FACEBOOK, id));
    }

    @Test
    void updateSocialId_googleId() {
        String id = "123";
        User user = new User();
        user.setSocialType("0");
        Mockito.lenient()
                .when(userRepository.findByGoogleId(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(userRepository.save(user))
                .thenReturn(user);
        User updated = userService.updateSocialId(user, SocialTypeEnum.GOOGLE, id);
        assertEquals(id, updated.getGoogleId());
        assertEquals(user.getSocialType(), updated.getSocialType());

    }

    @Test
    void updateSocialId_googleId_existError() {
        String id = "12345";
        User user = new User();
        user.setSocialType("0");
        user.setGoogleId(id);
        Mockito.lenient()
                .when(userRepository.findByGoogleId(anyString()))
                .thenReturn(Optional.of(user));
        assertThrows(ResponseStatusException.class,
                () -> userService.updateSocialId(user, SocialTypeEnum.GOOGLE, id));
    }

    @Test
    void updateUserMail() {
        User user = new User();
        user.setMail("old@mail.com");
        Mockito.lenient()
                .when(userRepository.save(user))
                .thenReturn(user);

        User updated = userService.updateUserMail(user, "new@mail.com");
        assertNotEquals("old@mail.com", updated.getMail());
    }
}