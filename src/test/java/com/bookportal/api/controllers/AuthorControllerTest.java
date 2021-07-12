package com.bookportal.api.controllers;

import com.bookportal.api.entity.Author;
import com.bookportal.api.model.AuthorDTO;
import com.bookportal.api.util.mapper.AuthorMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorControllerTest {

    private final String name = "ufuk gulerr";
    private final String about = "hakkinda";
    private static Long authorId;

    @Autowired
    private WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    AuthorMapper authorMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Order(1)
    void saveAuthor() throws Exception {
        AuthorDTO dto = new AuthorDTO();
        dto.setName(name);
        dto.setAbout(about);
        ResultActions resultActions = mockMvc.perform(post("/api/v1/author")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.about").value(about))
                .andExpect(status().isCreated());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        authorId = objectMapper.readValue(contentAsString, Author.class).getId();

    }

    @Test
    void saveAuthor_requiredFields_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/author")
                .content("{ \"about\":\"hakkinda\" }")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void saveAuthor_unauthorizedUser_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/author")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getAuthorsByPagination() throws Exception {
        mockMvc.perform(get("/api/v1/author")
                .param("page", "0")
                .param("size", "3")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageable.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageable.pageSize").value("3"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void getAuthorById() throws Exception {
        mockMvc.perform(get("/api/v1/author/{id}", authorId)
                .with(user("user").password("pass").roles("USER")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.about").value(about))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void updateAuthor() throws Exception {
        String nameUpdate = "ufuk-update";
        String aboutUpdate = "hakkinda-update";
        mockMvc.perform(put("/api/v1/author/{id}/update", 1)
                .content("{ \"name\":\"" + nameUpdate + "\", \"about\":\"" + aboutUpdate + "\" }")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(nameUpdate))
                .andExpect(jsonPath("$.about").value(aboutUpdate))
                .andExpect(status().isOk());
    }

    @Test
    void updateAuthor_requiredFields_name_return400() throws Exception {
        String nameUpdate = "ufuk-update";
        mockMvc.perform(put("/api/v1/author/{id}/update", 1)
                .content("{ \"name\":\"" + nameUpdate + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateAuthor_requiredFields_about_return400() throws Exception {
        String aboutUpdate = "hakkinda-update";
        mockMvc.perform(put("/api/v1/author/{id}/update", 1)
                .content("{ \"about\":\"" + aboutUpdate + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateAuthor_unauthorizedUser_returns400() throws Exception {
        mockMvc.perform(put("/api/v1/author/{id}/update", 1)
                .content("{ \"name\":\"updated\" }")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteAuthor() throws Exception {
        mockMvc.perform(delete("/api/v1/author/{id}/delete", 1)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAuthor_notExistingAuthor_returns400() throws Exception {
        mockMvc.perform(delete("/api/v1/author/{id}/delete", 13)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteAuthor_unauthorizedUser_returns400() throws Exception {
        mockMvc.perform(delete("/api/v1/author/{id}/delete", 1)
                .with(user("user").password("pass").roles("USER")))
                .andExpect(status().is4xxClientError());
    }
}