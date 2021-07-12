package com.bookportal.api.controllers;

import com.bookportal.api.entity.Author;
import com.bookportal.api.entity.Publisher;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.BookDTO;
import com.bookportal.api.model.BookUpdateDTO;
import com.bookportal.api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerTest {

    private static Long authorId = 1L;
    private static Long publisherId = 1L;

    @Autowired
    private WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

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
    void saveBook() throws Exception {
        beforeSaveBook();
        BookDTO dto = getBookDTO();
        mockMvc.perform(post("/api/v1/book")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(jsonPath("$.name").value(dto.getTitle()))
                .andExpect(jsonPath("$.page").value(dto.getPage()))
                .andExpect(jsonPath("$.publisher.id").value(dto.getPublisherId()))
                .andExpect(jsonPath("$.authors[0].id").value(dto.getAuthorIds()[0]))
                .andExpect(jsonPath("$.year").value(dto.getYear()))
                .andExpect(jsonPath("$.isPublished").value(false))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void saveBook_unauthorizedRole_throwsError() throws Exception {
        mockMvc.perform(post("/api/v1/book")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void saveBook_missingParams_throwsError() throws Exception {
        BookDTO dto = getBookDTO();
        dto.setTitle(null);
        mockMvc.perform(post("/api/v1/book")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void saveBook_publisherNotFound_throwsError() throws Exception {
        BookDTO dto = getBookDTO();
        dto.setPublisherId(454454545L);
        mockMvc.perform(post("/api/v1/book")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveBook_authorNotFound_throwsError() throws Exception {
        BookDTO dto = getBookDTO();
        dto.setAuthorIds(new Long[]{34343L, 3434342L});
        mockMvc.perform(post("/api/v1/book")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void saveBook2() throws Exception {
        BookDTO dto = getBookDTO2();
        mockMvc.perform(post("/api/v1/book")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(jsonPath("$.name").value(dto.getTitle()))
                .andExpect(jsonPath("$.page").value(dto.getPage()))
                .andExpect(jsonPath("$.publisher.id").value(dto.getPublisherId()))
                .andExpect(jsonPath("$.authors[0].id").value(dto.getAuthorIds()[0]))
                .andExpect(jsonPath("$.year").value(dto.getYear()))
                .andExpect(jsonPath("$.isPublished").value(false))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(3)
    void publishBook() throws Exception {
        mockMvc.perform(post("/api/v1/book/{id}/publish", 1)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(content().string("true"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void updateBook() throws Exception {
        BookUpdateDTO dto = getBookUpdateDTO();
        mockMvc.perform(put("/api/v1/book/{id}/update", 1)
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(jsonPath("$.name").value(dto.getTitle()))
                .andExpect(jsonPath("$.page").value(dto.getPage()))
                .andExpect(jsonPath("$.publisher.id").value(dto.getPublisherId()))
                .andExpect(jsonPath("$.authors[0].id").value(dto.getAuthorIds()[0]))
                .andExpect(jsonPath("$.year").value(dto.getYear()))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.isPublished").value(dto.getIsPublished()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateBook_publisherNotFound_throwsError() throws Exception {
        BookUpdateDTO dto = getBookUpdateDTO();
        dto.setPublisherId(3333L);
        mockMvc.perform(put("/api/v1/book/{id}/update", 1)
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_authorNotFound_throwsError() throws Exception {
        BookUpdateDTO dto = getBookUpdateDTO();
        dto.setAuthorIds(new Long[]{23434L, 3434343L});
        mockMvc.perform(put("/api/v1/book/{id}/update", 1)
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_missingParams_throwsError() throws Exception {
        BookUpdateDTO dto = getBookUpdateDTO();
        dto.setTitle(null);
        mockMvc.perform(put("/api/v1/book/{id}/update", 1)
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void unPublishBook() throws Exception {
        mockMvc.perform(post("/api/v1/book/{id}/unPublish", 1)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(content().string("true"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    void deleteBook() throws Exception {
        mockMvc.perform(delete("/api/v1/book/{id}/delete", 1)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBook_notFound() throws Exception {
        mockMvc.perform(delete("/api/v1/book/{id}/delete", 1)
                .with(user("user").password("pass").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_unauthorizedRole_throwsError() throws Exception {
        mockMvc.perform(delete("/api/v1/book/{id}/delete", 1)
                .with(user("user").password("pass").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    void findBookById() throws Exception {
        mockMvc.perform(get("/api/v1/book/{id}", 1)
                .with(user("user").password("pass").roles("USER")))
                .andExpect(jsonPath("$.book.active").value(true))
                .andExpect(jsonPath("$.book.id").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findBookById_notFound_throwsError() throws Exception {
        mockMvc.perform(get("/api/v1/book/{id}", 1)
                .with(user("user").password("pass").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooksByPagination() throws Exception {
        mockMvc.perform(get("/api/v1/book")
                .param("page", "0")
                .param("size", "11")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageable.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageable.pageSize").value("11"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void voteBook() throws Exception {
        mockMvc.perform(post("/api/v1/book/{id}/vote", 1)
                .param("vote", "3")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.vote").value("3"))
                .andExpect(jsonPath("$.book.id").value("1"))
                .andExpect(status().isOk());
    }

    @Test
    void voteBook_notFoundError() throws Exception {
        mockMvc.perform(post("/api/v1/book/{id}/vote", 1233)
                .param("vote", "3")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void voteBook_lessThanOne() throws Exception {
        mockMvc.perform(post("/api/v1/book/{id}/vote", 1)
                .param("vote", "0")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void voteBook_moreThanFive() throws Exception {
        mockMvc.perform(post("/api/v1/book/{id}/vote", 1)
                .param("vote", "6")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void findBookByNameOrAuthorName() throws Exception {
        mockMvc.perform(post("/api/v1/book/search")
                .param("text", "kitap")
                .with(user("user").password("pass").roles("USER")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    void beforeSaveBook() throws Exception {
        createPublisher();
        createAuthor();
        createUser();
    }

    private void createPublisher() throws Exception {
        ResultActions publisherSave = mockMvc.perform(post("/api/v1/publisher")
                .content("{ \"name\":\"yayinevi\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")));
        MvcResult result = publisherSave.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        publisherId = objectMapper.readValue(contentAsString, Publisher.class).getId();
    }

    private void createAuthor() throws Exception {
        ResultActions authorSave = mockMvc.perform(post("/api/v1/author")
                .content("{ \"name\":\"ufuk\", \"about\":\"hakkinda\" }")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("user").password("pass").roles("ADMIN")));

        MvcResult result2 = authorSave.andReturn();
        String contentAsString2 = result2.getResponse().getContentAsString();
        authorId = objectMapper.readValue(contentAsString2, Author.class).getId();
    }

    private void createUser() {
        User user = new User();
        user.setActive(true);
        user.setMail("user");
        user.setPassword("pass");
        user.setName("ufuk");
        user.setSurname("guler");
        userRepository.save(user);
    }

    private BookDTO getBookDTO() {
        BookDTO dto = new BookDTO();
        dto.setTitle("kitap");
        dto.setAuthorIds(new Long[]{authorId});
        dto.setPage(100);
        dto.setYear(1020);
        dto.setPublisherId(publisherId);
        return dto;
    }

    private BookUpdateDTO getBookUpdateDTO() {
        BookUpdateDTO dto = new BookUpdateDTO();
        dto.setTitle("kitap-update");
        dto.setAuthorIds(new Long[]{authorId});
        dto.setPage(333);
        dto.setPublisherId(publisherId);
        dto.setYear(3333);
        dto.setIsPublished(true);
        return dto;
    }

    private BookDTO getBookDTO2() {
        BookDTO dto = new BookDTO();
        dto.setTitle("kitap2");
        dto.setAuthorIds(new Long[]{authorId});
        dto.setPage(200);
        dto.setYear(2020);
        dto.setPublisherId(publisherId);
        return dto;
    }
}