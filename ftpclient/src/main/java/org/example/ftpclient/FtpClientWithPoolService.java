package org.example.ftpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FtpClientWithPoolService extends FtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpClientWithPoolService.class);

    @Autowired
    FtpClientProperties ftpClientProperties;
//    @Autowired
//    Testconf testconf;

    public FtpClientWithPoolService() {
        ftpclientManager= new FtpClientWithPoolManager(ftpClientProperties);
    }

//    public void print(){
//        logger.info("#############"+testconf.getIp()+"###########");
//    }

}




