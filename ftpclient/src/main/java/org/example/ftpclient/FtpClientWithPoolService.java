package org.example.ftpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FtpClientWithPoolService extends FtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpClientWithPoolService.class);

    @Autowired
    FtpClientProperties ftpClientProperties;

    public FtpClientWithPoolService() {
        ftpclientManager= new FtpClientWithPoolManager(ftpClientProperties);
    }

    public void print(){
        logger.info("#############"+ftpClientProperties.getFtphost()+"###########");
    }


    @Bean
    public FtpClientWithPoolService ftpClientWithPoolService() {
        return new FtpClientWithPoolService();
    }

}




