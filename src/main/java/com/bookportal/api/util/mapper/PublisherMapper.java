package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Publisher;
import com.bookportal.api.model.PublisherDTO;

public class PublisherMapper {
    public static Publisher dtoToPublisher(PublisherDTO dto){
        Publisher publisher = new Publisher();
        publisher.setName(dto.getName());
        return publisher;
    }
}
