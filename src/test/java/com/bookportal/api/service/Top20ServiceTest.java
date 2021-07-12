package com.bookportal.api.service;

import com.bookportal.api.entity.Top20;
import com.bookportal.api.repository.Top20Repository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class Top20ServiceTest {

    @Autowired
    Top20Service top20Service;

    @MockBean
    Top20Repository top20Repository;

    @Test
    void getTop20() {
        List<Top20> getTop20 = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            getTop20.add(new Top20());
        }
        Mockito.lenient()
                .when(top20Repository.findAll())
                .thenReturn(getTop20);
        List<Top20> top20 = top20Service.getTop20();
        assertEquals(20, top20.size());
    }
}