package com.example.rsyncperf.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;

@SpringBootTest
public class DownloadServiceTest {

    @Autowired
    DownloadService downloadService;

    @Test
    public void getFileList() {
        downloadService.getFileList();
    }

    @Test
    public void startThread() {
        downloadService.startThread();
    }
}