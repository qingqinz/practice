package com.example.ftpclient.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.util.StringUtils;

public class FTPClientWithoutPool implements FTPClientManager {

    FTPClientFactory ftpClientFactory;

    public FTPClientWithoutPool(String userName, String password) {
        FTPClientProperties ftpClientProperties = new FTPClientProperties();
        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)){
            ftpClientProperties.setUsername(userName);
            ftpClientProperties.setPassword(password);
        }
        ftpClientFactory = new FTPClientFactory(ftpClientProperties);
    }

    public FTPClientWithoutPool() {
        FTPClientProperties ftpClientProperties = new FTPClientProperties();
        ftpClientFactory = new FTPClientFactory(ftpClientProperties);
    }

    @Override
    public FTPClient borrowObject() {
        return ftpClientFactory.create();
    }

    @Override
    public void returnObject(FTPClient obj) {
        ftpClientFactory.destroyObject(obj);
    }
}
