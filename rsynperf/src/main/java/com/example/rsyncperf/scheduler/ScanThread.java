package com.example.rsyncperf.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(ScanThread.class);

    ScanService scanService;
    public ScanThread(ScanService scanService){
        this.scanService = scanService;
    }
    @Override
    public void run() {
        try {
            scanService.scan();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
