package com.example.demo.db;

import com.example.demo.db.DBservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBservice2 {

    @Autowired
    DBservice dBservice;
    public String getName(){
        return dBservice.getName();
    }
}
