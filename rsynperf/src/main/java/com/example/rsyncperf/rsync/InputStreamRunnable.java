package com.example.rsyncperf.rsync;

/**
 * @ClassName : InputStreamRunnable
 * @Description : 读取InputStream的线程
 * @Author : Mr.Wang
 * @Date : 2020-08-11 17:36
 **/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class InputStreamRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(InputStreamRunnable.class);

    BufferedReader bReader = null;
    private boolean isPrint;

    public InputStreamRunnable(InputStream is) {
        try {
            bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), StandardCharsets.UTF_8));
        } catch (Exception e) {
        }
    }

    public InputStreamRunnable(InputStream is, boolean isPrint) {
        try {
            bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), StandardCharsets.UTF_8));
            this.isPrint = isPrint;
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = bReader.readLine()) != null) {
                if (isPrint) {
                    logger.info(line);
                }
            }
            bReader.close();
        } catch (Exception ex) {
        }
    }
}
