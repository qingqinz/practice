package com.example.ftpclient.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class Worker implements Runnable{

    Logger logger = LoggerFactory.getLogger(Worker.class);
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
            logger.error("失败",e);
        } finally {
            countDownLatch.countDown();
        }
    }


}

