package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.HomePage;
import com.bookportal.api.entity.Top20;
import com.bookportal.api.model.HomePageResponseDTO;
import com.bookportal.api.model.Top20Response;
import com.bookportal.api.model.enums.HomePageEnum;
import com.bookportal.api.exception.CustomAlreadyExistException;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.HomePageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomePageService {
    private final HomePageRepository homePageRepository;
    private final BookService bookService;
    private final Top20Service top20Service;

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    @CacheEvict(value = {"homePage"})
    public HomePage save(HomePage homePage) {
        if (homePage.getType().getValue().equals(HomePageEnum.RECOMMENDED_BOOK.getValue())) {
            checkIfRecommendedExist();
        }
        Optional<HomePage> optionalHomePage = homePageRepository.findByTypeAndBook_Id(homePage.getType(), homePage.getBook().getId());
        if (optionalHomePage.isPresent()) {
            throw new CustomAlreadyExistException("Homepage model");
        }
        return homePageRepository.save(homePage);
    }

    @Cacheable("homePage")
    public HomePageResponseDTO homePageResponse() {
        HomePageResponseDTO dto = new HomePageResponseDTO();
        dto.setRecommendedBooksList(getRecommendedBooksList());
        dto.setEditorsChoiceList(getEditorChoiceList());
        dto.setRecommendedBook(getRecommendedBook());
        dto.setLastBook(getLast20Book());
        dto.setTopList(getTopList());
        return dto;
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    @CacheEvict(value = {"homePage"})
    public boolean delete(HomePage homePage) {
        Optional<HomePage> optionalHomePage = homePageRepository.findByTypeAndBook_Id(homePage.getType(), homePage.getBook().getId());
        if (optionalHomePage.isPresent()) {
            homePageRepository.delete(optionalHomePage.get());
            return true;
        }
        throw new CustomNotFoundException("Homepage model");
    }

    private List<HomePage> getRecommendedBooksList() {
        return homePageRepository.findAllByType(HomePageEnum.RECOMMENDED_LIST);
    }

    private List<HomePage> getEditorChoiceList() {
        return homePageRepository.findAllByType(HomePageEnum.EDITORS_CHOICE);
    }

    private HomePage getRecommendedBook() {
        Optional<HomePage> optionalHomePage = homePageRepository.findByType(HomePageEnum.RECOMMENDED_BOOK);
        return optionalHomePage.orElse(null);
    }

    private List<Top20Response> getTopList() {
        List<Top20Response> top20Responses = new ArrayList<>();
        List<Top20> top20 = top20Service.getTop20();
        top20.forEach(top -> {
            Top20Response response = new Top20Response();
            response.setBook(bookService.findByIdAndActiveTrue(top.getBookId()));
            response.setAverage(top.getAverage());
            response.setWr(top.getWr());
            top20Responses.add(response);
        });
        return top20Responses;
    }

    private List<Book> getLast20Book() {
        return bookService.getLastXBooks(20);
    }

    private void checkIfRecommendedExist() {
        Optional<HomePage> optionalHomePage = homePageRepository.findByType(HomePageEnum.RECOMMENDED_BOOK);
        if (optionalHomePage.isPresent())
            throw new CustomAlreadyExistException("Önerilen kitap");
    }
}
