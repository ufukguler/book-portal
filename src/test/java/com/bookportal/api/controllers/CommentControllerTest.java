package com.bookportal.api.controllers;

import com.bookportal.api.entity.Author;
import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Publisher;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.BookDTO;
import com.bookportal.api.model.CommentDTO;
import com.bookportal.api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CommentControllerTest {

    private static Long authorId = 1L;
    private static Long publisherId = 1L;
    private static Long bookId = 1L;
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
    void getCommentsByPagination() throws Exception {
        mockMvc.perform(get("/api/v1/comment")
                .param("page", "0")
                .param("size", "11")
                .with(user("userComment").password("").roles("USER")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageable.pageNumber").value("0"))
                .andExpect(jsonPath("$.pageable.pageSize").value("11"))
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    void saveComment() throws Exception {
        beforeSaveComment_PrepareBookData();
        CommentDTO dto = new CommentDTO();
        dto.setBookId(bookId);
        dto.setComment("guzel");
        mockMvc.perform(post("/api/v1/comment")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("userComment").password("pass").roles("USER")))
                .andExpect(jsonPath("$.book.id").value(bookId))
                .andExpect(jsonPath("$.comment").value(dto.getComment()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    void beforeSaveComment_PrepareBookData() throws Exception {
        createUser();
        createPublisher();
        createAuthor();
        createBook();
    }

    private void createPublisher() throws Exception {
        ResultActions publisherSave = mockMvc.perform(post("/api/v1/publisher")
                .content("{ \"name\":\"yayineviComment\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("userComment").password("pass").roles("ADMIN")));
        MvcResult result = publisherSave.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        publisherId = objectMapper.readValue(contentAsString, Publisher.class).getId();
    }

    private void createAuthor() throws Exception {
        ResultActions authorSave = mockMvc.perform(post("/api/v1/author")
                .content("{ \"name\":\"yazarComment\", \"about\":\"hakkinda\" }")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("userComment").password("pass").roles("ADMIN")));

        MvcResult result2 = authorSave.andReturn();
        String contentAsString2 = result2.getResponse().getContentAsString();
        authorId = objectMapper.readValue(contentAsString2, Author.class).getId();
    }

    private void createBook() throws Exception {
        BookDTO dto = getBookDTO();
        ResultActions resultActions = mockMvc.perform(post("/api/v1/book")
                .content(new ObjectMapper().writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user("userComment").password("pass").roles("ADMIN")))
                .andExpect(jsonPath("$.name").value(dto.getTitle()))
                .andExpect(jsonPath("$.page").value(dto.getPage()))
                .andExpect(jsonPath("$.publisher.id").value(dto.getPublisherId()))
                .andExpect(jsonPath("$.authors[0].id").value(dto.getAuthorIds()[0]))
                .andExpect(jsonPath("$.year").value(dto.getYear()))
                .andExpect(jsonPath("$.isPublished").value(false))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String content = resultActions.andReturn().getResponse().getContentAsString();
        bookId = objectMapper.readValue(content, Book.class).getId();

        mockMvc.perform(post("/api/v1/book/{id}/publish", bookId)
                .with(user("userComment").password("pass").roles("ADMIN")))
                .andExpect(content().string("true"))
                .andExpect(status().isOk());
    }

    private void createUser() {
        User user = new User();
        user.setActive(true);
        user.setMail("userComment");
        user.setPassword("pass");
        user.setName("ufuk");
        user.setSurname("ufuk");
        userRepository.save(user);
    }

    private BookDTO getBookDTO() {
        BookDTO dto = new BookDTO();
        dto.setTitle("bookComment");
        dto.setAuthorIds(new Long[]{authorId});
        dto.setPage(100);
        dto.setYear(1020);
        dto.setPublisherId(publisherId);
        return dto;
    }
}