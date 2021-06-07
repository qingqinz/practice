package com.example.demo.service;

import com.example.demo.db.DBservice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DBservice2Test {

    @Autowired
    DBservice dBservice;
    @Test
    void getName() {
        System.out.println(dBservice.getName());
    }
}