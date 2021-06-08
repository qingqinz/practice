package com.example.ftpclient.ftps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPSClientWithPoolUtil extends FTPSUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPSClientWithPoolUtil.class);

    private static FTPSClientWithPoolUtil ftpUtil;

    //单例，不要改为public
    private FTPSClientWithPoolUtil() {
        ftpClientManager= new FTPSClientWithPool();
    }

    public static FTPSClientWithPoolUtil getInstance(){
        if(null == ftpUtil){
            synchronized (FTPSClientWithPoolUtil.class){
                if (null==ftpUtil){
                    ftpUtil = new FTPSClientWithPoolUtil();
                }
            }
        }
        return ftpUtil;
    }



}




