package org.example.ftpclient;

import org.apache.commons.net.ftp.FTPClient;

public class FtpClientWithoutPoolManager implements FtpClientManager {

    FtpClientFactory ftpClientFactory;

    public FtpClientWithoutPoolManager(FtpClientProperties ftpclientProperties) {
        ftpClientFactory = new FtpClientFactory(ftpclientProperties);
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
