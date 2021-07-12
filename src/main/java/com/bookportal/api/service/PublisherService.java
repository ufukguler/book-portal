package com.bookportal.api.service;

import com.bookportal.api.entity.Publisher;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.exception.CustomAlreadyExistException;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public Publisher findById(Long id) {
        Optional<Publisher> byId = publisherRepository.findByIdAndActiveTrue(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.PUBLISHER.getValue());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Publisher save(Publisher publisher) {
        if (!publisherRepository.findByNameAndActiveTrue(publisher.getName()).isPresent()) {
            return publisherRepository.save(publisher);
        }
        throw new CustomAlreadyExistException(ExceptionItemsEnum.PUBLISHER.getValue());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Publisher update(Publisher publisher) {
        if (publisherRepository.findByIdAndActiveTrue(publisher.getId()).isPresent()) {
            return publisherRepository.save(publisher);
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.PUBLISHER.getValue());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public boolean delete(Long id) {
        Optional<Publisher> optionalPublisher = publisherRepository.findByIdAndActiveTrue(id);
        if (optionalPublisher.isPresent()) {
            Publisher pub = optionalPublisher.get();
            pub.setActive(false);
            Publisher save = publisherRepository.save(pub);
            return !save.isActive();
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.PUBLISHER.getValue());
    }

    public Page<Publisher> getAllByPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return publisherRepository.findAllByActiveTrue(pageable);
    }
}
