package com.bookportal.api.service;

import com.bookportal.api.entity.Publisher;
import com.bookportal.api.exception.CustomAlreadyExistException;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.PublisherRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(classes = PublisherService.class)
class PublisherServiceTest {

    @MockBean
    PublisherRepository publisherRepository;

    @Autowired
    PublisherService publisherService;

    @Test
    void findById() {
        Publisher publisher = initPublisher();

        Mockito.lenient()
                .when(publisherRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(publisher));

        Publisher byId = publisherService.findById(1L);
        assertNotNull(byId);
        assertEquals(byId.getName(), publisher.getName());
    }

    @Test
    void findById_throws_notFoundException() {
        Mockito.lenient()
                .when(publisherRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class, () -> publisherService.findById(1L));
    }

    @Test
    void save() {
        Publisher publisher = initPublisher();

        Mockito.lenient()
                .when(publisherRepository.save(any(Publisher.class)))
                .thenReturn(publisher);
        Mockito.lenient()
                .when(publisherRepository.findByNameAndActiveTrue(publisher.getName()))
                .thenReturn(Optional.empty());

        Publisher savedPublisher = publisherService.save(publisher);
        assertNotNull(publisher);
        assertEquals(publisher.getName(), savedPublisher.getName());
    }

    @Test
    void save_throws_alreadyExistException() {
        Publisher publisher = initPublisher();

        Mockito.lenient()
                .when(publisherRepository.findByNameAndActiveTrue(publisher.getName()))
                .thenReturn(Optional.of(publisher));

        assertThrows(CustomAlreadyExistException.class, () -> publisherService.save(publisher));
    }

    @Test
    void delete() {
        Long id = 1L;
        Publisher publisher = new Publisher();
        publisher.setActive(true);
        Mockito.lenient()
                .when(publisherRepository.findByIdAndActiveTrue(id))
                .thenReturn(Optional.of(publisher));
        Mockito.lenient()
                .when(publisherRepository.save(publisher))
                .thenReturn(publisher);
        boolean delete = publisherService.delete(id);
        assertTrue(delete);
    }

    @Test
    void delete_throws_notFoundException() {
        Mockito.lenient()
                .when(publisherRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> publisherService.delete(1L));
    }

    private Publisher initPublisher() {
        Publisher publisher = new Publisher();
        publisher.setName("test publisher");
        publisher.setActive(true);
        return publisher;
    }

    @Test
    void getAllByPagination() {
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);
        Page<Publisher> publisherPage = new PageImpl<>(Collections.singletonList(new Publisher()));

        Mockito.lenient()
                .when(publisherRepository.findAllByActiveTrue(pageable))
                .thenReturn(publisherPage);
        Page<Publisher> byPageSize = publisherService.getAllByPagination(page, size);
        Assertions.assertTrue(byPageSize.getSize() > 0);
        assertFalse(byPageSize.isEmpty());

    }

    @Test
    void update() {
        Publisher publisher = new Publisher();
        publisher.setName("yayınevi");
        publisher.setId(1L);

        Mockito.lenient()
                .when(publisherRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(publisher));
        Mockito.lenient()
                .when(publisherRepository.save(publisher))
                .thenReturn(publisher);
        Publisher updatedPublisher = publisherService.update(publisher);
        assertEquals(updatedPublisher.getName(), publisher.getName());
        assertEquals(updatedPublisher.getId(), publisher.getId());
    }

    @Test
    void update_throwsException_notFound() {
        Publisher publisher = new Publisher();
        publisher.setName("yayınevi");
        publisher.setId(1L);

        Mockito.lenient()
                .when(publisherRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> publisherService.update(publisher));
    }
}