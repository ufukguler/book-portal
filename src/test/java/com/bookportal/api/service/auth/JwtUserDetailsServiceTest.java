package com.bookportal.api.service.auth;

import com.bookportal.api.entity.Role;
import com.bookportal.api.entity.User;
import com.bookportal.api.exception.MyUserNotFoundException;
import com.bookportal.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(classes = JwtUserDetailsService.class)
class JwtUserDetailsServiceTest {

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @MockBean
    UserRepository userRepository;

    @Test
    void loadUserByUsername() {
        Role role = new Role();
        role.setName("role_user");
        User user = new User();
        user.setPassword("testpw");
        user.setMail("test@test.co");
        user.setRoles(Arrays.asList(role));
        Mockito.lenient()
                .when(userRepository.findByMail(anyString()))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getMail());
        assertEquals(userDetails.getUsername(), user.getMail());
        assertFalse(userDetails.getAuthorities().isEmpty());

    }

    @Test
    void loadUserByUsername_exception(){
        Mockito.lenient()
                .when(userRepository.findByMail(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(MyUserNotFoundException.class, () -> jwtUserDetailsService.loadUserByUsername("email"));
    }
}