package org.sample.ftp;

import org.apache.commons.net.ftp.FTPClient;

public class FTPClientWithoutPool implements FTPClientManager {

    FTPClientFactory ftpClientFactory;

    public FTPClientWithoutPool() {
        FTPClientProperties ftpClientProperties = new FTPClientProperties();
        ftpClientFactory = new FTPClientFactory(ftpClientProperties);
    }

    @Override
    public FTPClient borrowObject() {
        return ftpClientFactory.create();
    }

    @Override
    public FTPClient borrowObject(String userName, String password) throws Exception {
        return ftpClientFactory.create(userName, password);
    }

    @Override
    public void returnObject(FTPClient obj) {
        ftpClientFactory.destroyObject(obj);
    }
}
