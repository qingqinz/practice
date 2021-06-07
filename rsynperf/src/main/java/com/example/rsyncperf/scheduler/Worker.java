package com.example.rsyncperf.scheduler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class Worker implements Runnable{

    List<String> fileList;
    CountDownLatch countDownLatch;
    DownloadService downloadService;

    public Worker(List<String> fileList, CountDownLatch countDownLatch, DownloadService downloadService){
        this.fileList= fileList;
        this.countDownLatch = countDownLatch;
        this.downloadService = downloadService;
    }

    @Override
    public void run() {
        try {
            downloadService.download(fileList);
        } catch (Exception e){
            countDownLatch.countDown();
        } finally {
            countDownLatch.countDown();
        }
    }


}

