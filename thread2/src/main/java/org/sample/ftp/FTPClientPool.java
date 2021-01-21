package org.sample.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class FTPClientPool implements FTPClientManager {

    private GenericObjectPool<FTPClient> genericObjectPool;

    public FTPClientPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setTestOnBorrow(true);
        FTPClientProperties ftpClientProperties = new FTPClientProperties();
        FTPClientFactory ftpClientFactory = new FTPClientFactory(ftpClientProperties);
        this.genericObjectPool = new GenericObjectPool<>(ftpClientFactory, genericObjectPoolConfig);
    }


    @Override
    public FTPClient borrowObject() throws Exception {
        return genericObjectPool.borrowObject();
    }

    @Override
    public FTPClient borrowObject(String userName, String password) throws Exception {
        return null;
    }

    @Override
    public void returnObject(FTPClient obj) {
        genericObjectPool.returnObject(obj);
    }
}
