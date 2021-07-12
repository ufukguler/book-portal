package com.bookportal.api.model;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.HomePage;
import lombok.Data;

import java.util.List;

@Data
public class HomePageResponseDTO {
    private List<HomePage> recommendedBooksList;
    private List<HomePage> editorsChoiceList;
    private List<Top20Response> topList;
    private List<Book> lastBook;
    private HomePage recommendedBook;
}
