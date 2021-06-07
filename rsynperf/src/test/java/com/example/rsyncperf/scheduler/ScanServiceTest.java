package com.example.rsyncperf.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class ScanServiceTest {

    @Autowired
    ScanService scanService;

    @Test
    public void scanTest() throws Exception {
        scanService.scan();
    }



}