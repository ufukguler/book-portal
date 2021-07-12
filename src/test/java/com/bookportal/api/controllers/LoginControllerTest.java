package com.bookportal.api.controllers;

import com.bookportal.api.model.enums.SocialTypeEnum;
import com.bookportal.api.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mockMvc;

    private static String token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Order(1)
    void createAuthenticationToken() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setMail("user@appmedia.com");
        dto.setPassword("123");

        String contentAsString = mockMvc.perform(post("/api/v1/login")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getMail(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
    }

    @Test
    @Order(3)
    void createSocialAuthenticationToken_facebook() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("social_facebook@appmedia.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType(SocialTypeEnum.FACEBOOK.getValue());
        dto.setFacebookId("facebook_new");
        dto.setGoogleId("");
        dto.setPpUrl("");

        String contentAsString = mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getMail(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
        token = jwtResponseDTO.getToken();
    }

    @Test
    @Order(4)
    void createSocialAuthenticationToken_facebook_reLogin() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("social_facebook@appmedia.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType(SocialTypeEnum.FACEBOOK.getValue());
        dto.setFacebookId("facebook_new");
        dto.setGoogleId("");
        dto.setPpUrl("");

        String contentAsString = mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getMail(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
    }

    @Test
    @Order(5)
    void createSocialAuthenticationToken_google() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("social_google@appmedia.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType(SocialTypeEnum.GOOGLE.getValue());
        dto.setFacebookId("");
        dto.setGoogleId("google_new");
        dto.setPpUrl("");

        String contentAsString = mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getMail(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
    }

    @Test
    @Order(6)
    void createSocialAuthenticationToken_google_reLogin() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("social_google@appmedia.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType(SocialTypeEnum.GOOGLE.getValue());
        dto.setFacebookId("");
        dto.setGoogleId("google_new");
        dto.setPpUrl("");

        String contentAsString = mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getMail(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
    }

    @Test
    @Order(7)
    void createSocialAuthenticationToken_loginToGoogleWithNewFacebook() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("social_google@appmedia.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType(SocialTypeEnum.FACEBOOK.getValue());
        dto.setFacebookId("facebook_merge");
        dto.setGoogleId("");
        dto.setPpUrl("");

        String contentAsString = mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getMail(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
    }


    @Test
    void createSocialAuthenticationToken_facebook_reLogin_withNewMail() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("social_facebook_new@appmedia.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType(SocialTypeEnum.FACEBOOK.getValue());
        dto.setFacebookId("facebook_new");
        dto.setGoogleId("");
        dto.setPpUrl("");

        String contentAsString = mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getMail(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
    }

    @Test
    @Order(2)
    void createSocialAuthenticationToken_socialTypeEnum_NotExist() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("typeEnum@google.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType("3");
        dto.setFacebookId("facebook");
        dto.setGoogleId("");
        dto.setPpUrl("");

        mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse();
    }


    @Test
    void createSocialAuthenticationToken_facebookIdLength_throws400() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("typeEnum@facebook.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType(SocialTypeEnum.FACEBOOK.getValue());
        dto.setFacebookId("");
        dto.setGoogleId("");
        dto.setPpUrl("");

        mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSocialAuthenticationToken_googleIdLength_throws400() throws Exception {
        SocialDTO dto = new SocialDTO();
        dto.setMail("social@appmedia.com");
        dto.setName("name");
        dto.setSurname("surname");
        dto.setSocialType(SocialTypeEnum.GOOGLE.getValue());
        dto.setFacebookId("");
        dto.setGoogleId("");
        dto.setPpUrl("");

        mockMvc.perform(post("/api/v1/login/social")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshAuthenticationToken() throws Exception {
        JwtRefreshDTO dto = new JwtRefreshDTO();
        dto.setMail("social_facebook@appmedia.com");
        dto.setToken(token);

        String contentAsString = mockMvc.perform(post("/api/v1/login/refresh")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getMail(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
    }

    @Test
    void refreshAuthenticationToken_UNAUTHORIZED() throws Exception {
        JwtRefreshDTO dto = new JwtRefreshDTO();
        dto.setMail("ufuk@appmedia.com");
        dto.setToken(token);

        mockMvc.perform(post("/api/v1/login/refresh")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGuestToken() throws Exception {
        GuestDTO dto = new GuestDTO();
        dto.setAndroidID(UUID.randomUUID().toString());

        String contentAsString = mockMvc.perform(post("/api/v1/login/guest")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JwtResponseDTO jwtResponseDTO = objectMapper.readValue(contentAsString, JwtResponseDTO.class);
        assertEquals(dto.getAndroidID(), jwtResponseDTO.getUsername());
        assertEquals(200, jwtResponseDTO.getStatus());
        assertNotNull(jwtResponseDTO.getToken());
        assertNotNull(jwtResponseDTO.getIssuedAt());
        assertNotNull(jwtResponseDTO.getExpireAt());
    }

    @Test
    void createAuthenticationToken_DisabledException() {
        /* TODO
        UserDTO dto = new UserDTO();
        dto.setMail("inactive@appmedia.com");
        dto.setPassword("123");
        mockMvc.perform(post("/api/v1/login")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
         */
    }

    @Test
    void createAuthenticationToken_BadCredentialsException() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setMail("user@appmedia.com");
        dto.setPassword("123456");

        mockMvc.perform(post("/api/v1/login")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}