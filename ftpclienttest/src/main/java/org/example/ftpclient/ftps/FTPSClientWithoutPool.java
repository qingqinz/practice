package org.example.ftpclient.ftps;

import org.apache.commons.net.ftp.FTPSClient;
import org.example.ftpclient.ftp.FTPClientProperties;
import org.springframework.util.StringUtils;

public class FTPSClientWithoutPool implements FTPSClientManager {

    FTPSClientFactory ftpClientFactory;

    public FTPSClientWithoutPool(String userName, String password) {
        FTPClientProperties ftpClientProperties = new FTPClientProperties();
        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)){
            ftpClientProperties.setUsername(userName);
            ftpClientProperties.setPassword(password);
        }
        ftpClientFactory = new FTPSClientFactory(ftpClientProperties);
    }

    public FTPSClientWithoutPool() {
        FTPClientProperties ftpClientProperties = new FTPClientProperties();
        ftpClientFactory = new FTPSClientFactory(ftpClientProperties);
    }

    @Override
    public FTPSClient borrowObject() {
        return ftpClientFactory.create();
    }

    @Override
    public void returnObject(FTPSClient obj) {
        ftpClientFactory.destroyObject(obj);
    }
}
