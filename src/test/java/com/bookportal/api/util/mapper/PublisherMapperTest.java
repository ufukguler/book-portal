package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Publisher;
import com.bookportal.api.model.PublisherDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PublisherMapperTest {

    @Test
    void dtoToPublisher() {
        PublisherDTO dto = new PublisherDTO();
        dto.setName("yayınevi");

        Publisher publisher = PublisherMapper.dtoToPublisher(dto);
        Assertions.assertEquals(publisher.getName(), dto.getName());
    }
}