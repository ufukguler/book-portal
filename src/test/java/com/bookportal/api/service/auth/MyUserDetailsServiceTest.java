package com.bookportal.api.service.auth;

import com.bookportal.api.entity.Role;
import com.bookportal.api.entity.User;
import com.bookportal.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = MyUserDetailsService.class)
class MyUserDetailsServiceTest {

    @Autowired
    MyUserDetailsService myUserDetailsService;

    @MockBean
    UserRepository userRepository;

    @Test
    void loadUserByUsername() {
        Role role = new Role();
        role.setName("some_role");
        User user = new User();
        user.setMail("test@mail.co");
        user.setRoles(Arrays.asList(role));
        user.setPassword("somepass");

        Mockito.lenient()
                .when(userRepository.findByMail(user.getMail()))
                .thenReturn(Optional.of(user));
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getMail());

        assertEquals(userDetails.getUsername(), user.getMail());
        assertTrue(userDetails != null);
        assertTrue(userDetails.getAuthorities().size() > 0);
        assertTrue(userDetails.getPassword() != null);
    }
}