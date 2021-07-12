package com.bookportal.api.controllers;

import com.bookportal.api.entity.Favourite;
import com.bookportal.api.entity.Quote;
import com.bookportal.api.entity.User;
import com.bookportal.api.entity.Vote;
import com.bookportal.api.service.FavouriteService;
import com.bookportal.api.service.QuoteService;
import com.bookportal.api.service.UserService;
import com.bookportal.api.service.VoteService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/userprofile")
@RequiredArgsConstructor
public class AppMediaProfileController {
    private final FavouriteService favouriteService;
    private final UserService userService;
    private final QuoteService quoteService;
    private final VoteService voteService;

    @GetMapping("/myQuotes")
    @ApiOperation(value = "favourite list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Quote> findFavouriteQuotes() {
        Optional<User> currentUser = userService.getCurrentUser();
        List<Favourite> favouritesByUser = favouriteService.findFavouritesByUser(currentUser.get().getId());
        List<Long> ids = favouritesByUser
                .stream()
                .map(favourite -> favourite.getQuote().getId())
                .collect(Collectors.toList());
        return quoteService.findQuotestByIds(ids);
    }

    @GetMapping("/votedBooks")
    @ApiOperation(value = "Voted books")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Vote> findVotedBooks() {
        Long currentUserId = userService.getCurrentUser().get().getId();
        return voteService.findVoteByUser(currentUserId);
    }
}
