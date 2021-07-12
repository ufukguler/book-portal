package com.bookportal.api.auth;

import com.bookportal.api.model.JwtResponseDTO;
import com.bookportal.api.model.UserDTO;
import com.bookportal.api.service.auth.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
class JwtTokenUtilTest {
    private static String token;
    private static UserDTO dto;

    MockMvc mockMvc;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        dto = new UserDTO();
        dto.setMail("user@appmedia.com");
        dto.setPassword("123");
        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/login")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        token = objectMapper.readValue(response.getContentAsString(), JwtResponseDTO.class).getToken();
    }

    @Test
    void getUsernameFromToken() {
        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(dto.getMail(), username);
    }

    @Test
    void validateToken() {
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(dto.getMail());
        Boolean aBoolean = jwtTokenUtil.validateToken(token, userDetails);
        assertTrue(aBoolean);
    }
}