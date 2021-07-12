package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.HomePage;
import com.bookportal.api.entity.Top20;
import com.bookportal.api.exception.CustomAlreadyExistException;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.model.HomePageResponseDTO;
import com.bookportal.api.model.enums.HomePageEnum;
import com.bookportal.api.repository.HomePageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

@SpringBootTest(classes = HomePageService.class)
class HomePageServiceTest {

    @MockBean
    HomePageRepository repository;

    @MockBean
    BookService bookService;

    @MockBean
    Top20Service top20Service;

    @Autowired
    HomePageService service;

    @Test
    void save() {
        HomePage homePage = initHomePage(HomePageEnum.RECOMMENDED_LIST, 3L);

        Mockito.lenient()
                .when(repository.findByTypeAndBook_Id(homePage.getType(), homePage.getBook().getId()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(repository.save(homePage))
                .thenReturn(homePage);

        HomePage saved = service.save(homePage);
        Assertions.assertNotNull(homePage);
        Assertions.assertEquals(saved.getBook(), homePage.getBook());
        Assertions.assertEquals(saved.getType(), homePage.getType());
    }

    @Test
    void save_recommendedBook() {
        HomePage homePage = initHomePage(HomePageEnum.RECOMMENDED_BOOK, 3L);

        Mockito.lenient()
                .when(repository.findByType(HomePageEnum.RECOMMENDED_BOOK))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(repository.findByTypeAndBook_Id(homePage.getType(), homePage.getBook().getId()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(repository.save(homePage))
                .thenReturn(homePage);

        HomePage saved = service.save(homePage);
        Assertions.assertNotNull(homePage);
        Assertions.assertEquals(saved.getBook(), homePage.getBook());
        Assertions.assertEquals(saved.getType(), homePage.getType());
    }

    @Test
    void save_throwsExist() {
        HomePage homePage = initHomePage(HomePageEnum.RECOMMENDED_LIST, 3L);

        Mockito.lenient()
                .when(repository.findByTypeAndBook_Id(homePage.getType(), homePage.getBook().getId()))
                .thenReturn(Optional.of(homePage));

        Assertions.assertThrows(CustomAlreadyExistException.class, () -> service.save(homePage));
    }

    @Test
    void save_recommendedBook_throwsExist() {
        HomePage homePage = initHomePage(HomePageEnum.RECOMMENDED_BOOK, 1L);

        Mockito.lenient()
                .when(repository.findByType(HomePageEnum.RECOMMENDED_BOOK))
                .thenReturn(Optional.of(homePage));

        Assertions.assertThrows(CustomAlreadyExistException.class, () -> service.save(homePage));

    }

    @Test
    void homePageResponse() {
        HomePage homePage = initHomePage(HomePageEnum.RECOMMENDED_BOOK, 1L);
        List<Top20> top20List = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            top20List.add(new Top20());
        }
        Mockito.lenient()
                .when(repository.findAllByType(HomePageEnum.RECOMMENDED_LIST))
                .thenReturn(Collections.singletonList(new HomePage()));
        Mockito.lenient()
                .when(repository.findAllByType(HomePageEnum.EDITORS_CHOICE))
                .thenReturn(Collections.singletonList(new HomePage()));
        Mockito.lenient()
                .when(repository.findByType(HomePageEnum.RECOMMENDED_BOOK))
                .thenReturn(Optional.of(homePage));
        Mockito.lenient()
                .when(top20Service.getTop20())
                .thenReturn(top20List);

        HomePageResponseDTO dto = service.homePageResponse();

        Assertions.assertFalse(dto.getRecommendedBooksList().isEmpty());
        Assertions.assertFalse(dto.getEditorsChoiceList().isEmpty());
        Assertions.assertNotNull(dto.getRecommendedBook());
    }

    @Test
    void homePageResponse_recommendedBook_null() {
        Mockito.lenient()
                .when(repository.findAllByType(HomePageEnum.RECOMMENDED_LIST))
                .thenReturn(Collections.singletonList(new HomePage()));
        Mockito.lenient()
                .when(repository.findAllByType(HomePageEnum.EDITORS_CHOICE))
                .thenReturn(Collections.singletonList(new HomePage()));
        Mockito.lenient()
                .when(repository.findByType(HomePageEnum.RECOMMENDED_BOOK))
                .thenReturn(Optional.empty());

        HomePageResponseDTO dto = service.homePageResponse();

        Assertions.assertFalse(dto.getRecommendedBooksList().isEmpty());
        Assertions.assertFalse(dto.getEditorsChoiceList().isEmpty());
        Assertions.assertNull(dto.getRecommendedBook());
    }

    @Test
    void delete() {
        HomePage homePage = initHomePage(HomePageEnum.RECOMMENDED_BOOK, 1L);

        Mockito.lenient()
                .when(repository.findByTypeAndBook_Id(homePage.getType(), homePage.getBook().getId()))
                .thenReturn(Optional.of(homePage));

        boolean delete = service.delete(homePage);
        Assertions.assertTrue(delete);
    }

    @Test
    void delete_notFound_throwsCustomNotFoundException() {
        HomePage homePage = initHomePage(HomePageEnum.RECOMMENDED_BOOK, 1L);
        Mockito.lenient()
                .when(repository.findByTypeAndBook_Id(homePage.getType(), homePage.getBook().getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(CustomNotFoundException.class, () -> service.delete(homePage));
    }

    HomePage initHomePage(HomePageEnum homePageEnum, Long id) {
        HomePage homePage = new HomePage();
        homePage.setType(homePageEnum);
        homePage.setBook(initBookWithID(id));
        return homePage;
    }

    Book initBookWithID(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }
}