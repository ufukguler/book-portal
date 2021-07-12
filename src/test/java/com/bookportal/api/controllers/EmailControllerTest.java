package com.bookportal.api.controllers;

import com.bookportal.api.entity.EmailConfirm;
import com.bookportal.api.entity.PasswordReset;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.PasswordResetDTO;
import com.bookportal.api.model.UserRegisterDTO;
import com.bookportal.api.repository.EmailConfirmRepository;
import com.bookportal.api.repository.PasswordResetRepository;
import com.bookportal.api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmailControllerTest {

    @Autowired
    PasswordResetRepository passwordResetRepository;

    @Autowired
    EmailConfirmRepository emailConfirmRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    MockMvc mockMvc;

    UserRegisterDTO dto = getUserRegisterDTO();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Order(1)
    void before_registerUser() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated());

        List<EmailConfirm> emailConfirmList = emailConfirmRepository.findAll();
        EmailConfirm emailConfirm = emailConfirmList.get(emailConfirmList.size() - 1);
        String secretKey = emailConfirm.getSecretKey();
        mockMvc.perform(get("/sendMail/confirmEmail")
                .param("email", dto.getMail())
                .param("key", secretKey))
                .andExpect(status().isOk());
    }

    @Test
    void confirmEmail_userNotFound() throws Exception {
        List<EmailConfirm> emailConfirmList = emailConfirmRepository.findAll();
        EmailConfirm emailConfirm = emailConfirmList.get(emailConfirmList.size() - 1);
        String secretKey = emailConfirm.getSecretKey();
        mockMvc.perform(get("/sendMail/confirmEmail")
                .param("email", "someUserNotFound")
                .param("key", secretKey))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmEmail_alreadyConfirmed() throws Exception {
        List<EmailConfirm> emailConfirmList = emailConfirmRepository.findAll();
        EmailConfirm emailConfirm = emailConfirmList.get(emailConfirmList.size() - 1);
        String secretKey = emailConfirm.getSecretKey();
        mockMvc.perform(get("/sendMail/confirmEmail")
                .param("email", dto.getMail())
                .param("key", secretKey))
                .andExpect(status().isAlreadyReported());
    }


    @Test
    @Order(2)
    void resetPassword() throws Exception {
        createUser();
        mockMvc.perform(get("/sendMail/resetPassword")
                .param("email", "test@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    void resetPassword_userNotFound_throwsError() throws Exception {
        mockMvc.perform(get("/sendMail/resetPassword")
                .param("email", "user@test.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    void updatePassword() throws Exception {
        List<PasswordReset> passwordResets = passwordResetRepository.findAll();
        String secretKey = passwordResets.get(passwordResets.size() - 1).getSecretKey();

        PasswordResetDTO passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setEmail(dto.getMail());
        passwordResetDTO.setNewPass("newPass");
        passwordResetDTO.setKey(secretKey);

        mockMvc.perform(post("/sendMail/resetPassword")
                .content(new ObjectMapper().writeValueAsString(passwordResetDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassword_userNotFound() throws Exception {
        List<PasswordReset> passwordResets = passwordResetRepository.findAll();
        String secretKey = passwordResets.get(passwordResets.size() - 1).getSecretKey();

        PasswordResetDTO passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setEmail("aUser@gmail.com");
        passwordResetDTO.setNewPass("newPass");
        passwordResetDTO.setKey(secretKey);

        mockMvc.perform(post("/sendMail/resetPassword")
                .content(new ObjectMapper().writeValueAsString(passwordResetDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private void createUser() {
        User user = new User();
        user.setActive(true);
        user.setMail("userMail");
        user.setPassword("pass");
        user.setName("ufuk");
        user.setSurname("guler");
        userRepository.save(user);
    }

    private UserRegisterDTO getUserRegisterDTO() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setName("name");
        dto.setSurname("surname");
        dto.setMail("test@test.com");
        dto.setPassword("password");
        return dto;
    }
}