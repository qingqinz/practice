package org.example.ftpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoadService {
   Logger logger = LoggerFactory.getLogger(LoadService.class);
    @Autowired
    FtpClientWithPoolService ftpclientWithPoolUtil;

    public void upload() throws Exception {
        logger.info("this is UploadByFtpclientService");
        ftpclientWithPoolUtil.print();
//        ftpclientWithPoolUtil.upload("/Users/zqq/Downloads/bankfilepath/ftptest/","1233-aofa-2020.log","");
    }
}
