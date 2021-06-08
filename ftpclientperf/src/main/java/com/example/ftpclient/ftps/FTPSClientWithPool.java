package com.example.ftpclient.ftps;

import com.example.ftpclient.ftp.FTPClientProperties;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class FTPSClientWithPool implements FTPSClientManager {

    private GenericObjectPool<FTPSClient> genericObjectPool;

    public FTPSClientWithPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setTestOnBorrow(true);
        FTPClientProperties ftpClientProperties = new FTPClientProperties();
        FTPSClientFactory ftpClientFactory = new FTPSClientFactory(ftpClientProperties);
        this.genericObjectPool=new GenericObjectPool<>(ftpClientFactory,genericObjectPoolConfig);
    }

    @Override
    public FTPSClient borrowObject() throws Exception {
        return genericObjectPool.borrowObject();
    }

    @Override
    public void returnObject(FTPSClient obj) {
        genericObjectPool.returnObject(obj);
    }
}
