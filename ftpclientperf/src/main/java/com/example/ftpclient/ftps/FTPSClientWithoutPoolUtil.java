package com.example.ftpclient.ftps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPSClientWithoutPoolUtil extends FTPSUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPSClientWithoutPoolUtil.class);

    public FTPSClientWithoutPoolUtil(String userName, String password) {
        ftpClientManager= new FTPSClientWithoutPool(userName,password);
    }

    public FTPSClientWithoutPoolUtil() {
        ftpClientManager= new FTPSClientWithoutPool();
    }

}




