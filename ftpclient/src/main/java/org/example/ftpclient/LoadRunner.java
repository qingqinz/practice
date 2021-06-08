package org.example.ftpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class LoadRunner implements ApplicationRunner {
    Logger logger = LoggerFactory.getLogger(LoadRunner.class);
    @Autowired
    LoadService loadService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
//        loadService.upload();

    }
}
