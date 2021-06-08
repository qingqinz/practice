package org.example.ftpclient.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class FTPClientWithPool implements FTPClientManager {

    private GenericObjectPool<FTPClient> genericObjectPool;

    public FTPClientWithPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setTestOnBorrow(true);
        FTPClientProperties ftpClientProperties = new FTPClientProperties();
        FTPClientFactory ftpClientFactory = new FTPClientFactory(ftpClientProperties);
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
