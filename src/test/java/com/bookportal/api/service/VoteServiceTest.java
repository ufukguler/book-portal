package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.User;
import com.bookportal.api.entity.Vote;
import com.bookportal.api.repository.VoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(classes = VoteService.class)
class VoteServiceTest {

    @MockBean
    VoteRepository voteRepository;

    @MockBean
    UserService userService;

    @Autowired
    VoteService voteService;

    @Test
    void findVoteByUser() {
        Mockito.lenient()
                .when(voteRepository.findAllByUserId(anyLong()))
                .thenReturn(new ArrayList<>());
        List<Vote> voteByUser = voteService.findVoteByUser(1L);
        Assertions.assertNotNull(voteByUser);
    }

    @Test
    void voteBook_newVote() {
        int vote = 4;
        Book book = new Book();
        book.setId(3L);
        User user = new User();
        user.setId(4L);
        Vote voteObj = new Vote();
        voteObj.setVote(vote);
        voteObj.setUser(user);
        voteObj.setBook(book);

        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(user));
        Mockito.lenient()
                .when(voteRepository.findByUserIdAndBookId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(voteRepository.save(any()))
                .thenReturn(voteObj);

        Vote savedVote = voteService.voteBook(book, vote);
        Assertions.assertNotNull(savedVote);
        Assertions.assertEquals(vote, savedVote.getVote());
        Assertions.assertEquals(book, savedVote.getBook());
        Assertions.assertEquals(user, savedVote.getUser());

    }

    @Test
    void voteBook_existingVote() {
        int vote = 4;
        int newVote = 5;
        Book book = new Book();
        book.setId(3L);
        User user = new User();
        user.setId(4L);
        Vote voteObj = new Vote();
        voteObj.setVote(vote);
        voteObj.setUser(user);
        voteObj.setBook(book);

        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(user));
        Mockito.lenient()
                .when(voteRepository.findByUserIdAndBookId(anyLong(), anyLong()))
                .thenReturn(Optional.of(voteObj));
        Mockito.lenient()
                .when(voteRepository.save(any()))
                .thenReturn(voteObj);

        Vote savedVote = voteService.voteBook(book, newVote);
        Assertions.assertNotNull(savedVote);
        Assertions.assertEquals(newVote, savedVote.getVote());
    }
}