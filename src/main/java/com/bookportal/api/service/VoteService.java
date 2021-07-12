package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.User;
import com.bookportal.api.entity.Vote;
import com.bookportal.api.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final UserService userService;

    public List<Vote> findVoteByUser(Long id) {
        return voteRepository.findAllByUserId(id);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Vote voteBook(Book book, int v) {
        User user = userService.getCurrentUser().get();
        Optional<Vote> byUserIdAndBookId = voteRepository.findByUserIdAndBookId(user.getId(), book.getId());
        if(byUserIdAndBookId.isPresent()){
            Vote vote = byUserIdAndBookId.get();
            vote.setVote(v);
            return voteRepository.save(vote);
        }

        Vote vote = new Vote();
        vote.setBook(book);
        vote.setUser(userService.getCurrentUser().get());
        vote.setVote(v);
        return voteRepository.save(vote);
    }
}
