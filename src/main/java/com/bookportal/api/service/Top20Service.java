package com.bookportal.api.service;

import com.bookportal.api.entity.Top20;
import com.bookportal.api.repository.Top20Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Top20Service {
    private final Top20Repository top20Repository;

    public List<Top20> getTop20() {
        List<Top20> all = top20Repository.findAll();
        all.sort(Comparator.comparing(Top20::getWr).reversed());
        return all;
    }
}
