package org.example.ftpclient.ftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPClientWithoutPoolUtil extends FTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPClientWithoutPoolUtil.class);

    public FTPClientWithoutPoolUtil(String userName,String password) {
        ftpClientManager= new FTPClientWithoutPool(userName,password);
    }

    public FTPClientWithoutPoolUtil() {
        ftpClientManager= new FTPClientWithoutPool();
    }

}




