package com.bookportal.api.util.mapper;

import com.bookportal.api.model.QuoteDTO;
import com.bookportal.api.entity.Quote;

public class QuoteMapper {
    public static Quote quoteDtoToQuote(QuoteDTO dto) {
        Quote quote = new Quote();
        quote.setQuote(dto.getQuote());
        return quote;
    }
}
