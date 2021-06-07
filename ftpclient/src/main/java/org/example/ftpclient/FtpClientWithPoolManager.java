package org.example.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class FtpClientWithPoolManager implements FtpClientManager {

    private GenericObjectPool<FTPClient> genericObjectPool;
    private FtpClientFactory ftpClientFactory;

    public FtpClientWithPoolManager(FtpClientProperties ftpclientProperties) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setTestOnBorrow(true);
        ftpClientFactory = new FtpClientFactory(ftpclientProperties);
        this.genericObjectPool=new GenericObjectPool<>(ftpClientFactory,genericObjectPoolConfig);
    }

    @Override
    public FTPClient borrowObject() throws Exception {
        return genericObjectPool.borrowObject();
    }

    @Override
    public void returnObject(FTPClient obj) {
        genericObjectPool.returnObject(obj);
    }
}
