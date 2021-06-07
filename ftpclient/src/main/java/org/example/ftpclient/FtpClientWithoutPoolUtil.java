package org.example.ftpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FtpClientWithoutPoolUtil extends FtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpClientWithoutPoolUtil.class);

    public FtpClientWithoutPoolUtil(FtpClientProperties ftpClientProperties) {
        ftpclientManager= new FtpClientWithoutPoolManager(ftpClientProperties);
    }

}




