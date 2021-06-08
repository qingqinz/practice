package com.example.ftpclient.ftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPClientWithPoolUtil extends  FTPUtil{
    private static final Logger logger = LoggerFactory.getLogger(FTPClientWithPoolUtil.class);

    private static FTPClientWithPoolUtil ftpUtil;

    //单例，不要改为public
    private FTPClientWithPoolUtil() {
        ftpClientManager= new FTPClientWithPool();
    }

    public static FTPClientWithPoolUtil getInstance(){
        if(null == ftpUtil){
            synchronized (FTPClientWithPoolUtil.class){
                if (null==ftpUtil){
                    ftpUtil = new FTPClientWithPoolUtil();
                }
            }
        }
        return ftpUtil;
    }



}




