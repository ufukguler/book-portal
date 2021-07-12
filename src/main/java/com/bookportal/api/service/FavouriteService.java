package com.bookportal.api.service;

import com.bookportal.api.entity.Favourite;
import com.bookportal.api.entity.Quote;
import com.bookportal.api.entity.User;
import com.bookportal.api.repository.FavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final UserService userService;
    private final QuoteService quoteService;

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public boolean addQuoteToFavourite(Long quoteId) {
        Optional<User> currentUser = userService.getCurrentUser();
        Quote quote = quoteService.findByIdAndActiveTrue(quoteId);
        Optional<Favourite> byQuoteIdAndUserId = favouriteRepository.findByQuoteIdAndUserId(quoteId, currentUser.get().getId());
        if (byQuoteIdAndUserId.isPresent()) {
            Favourite favourite = byQuoteIdAndUserId.get();
            favourite.setActive(!favourite.isActive());
            quoteService.decreaseQuoteCount(quote);
            favouriteRepository.save(favourite);
            return true;
        }
        Favourite fav = new Favourite();
        fav.setQuote(quote);
        fav.setUser(currentUser.get());
        quoteService.increaseQuoteCount(quote);
        favouriteRepository.save(fav);
        return true;

    }

    public List<Favourite> findFavouritesByQuote(Long id) {
        return favouriteRepository.findAllByQuoteIdAndActiveTrue(id);
    }

    public List<Favourite> findFavouritesByUser(Long id) {
        return favouriteRepository.findAllByUserIdAndActiveTrue(id);
    }
}
