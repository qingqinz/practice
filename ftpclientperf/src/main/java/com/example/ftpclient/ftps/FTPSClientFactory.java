package com.example.ftpclient.ftps;

import com.example.ftpclient.ftp.FTPClientProperties;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * FTPSClient工厂类，通过FTPSClient工厂提供FTPSClient实例的创建和销毁
 */
public class FTPSClientFactory extends BasePooledObjectFactory<FTPSClient> {
    private static final Logger logger= LoggerFactory.getLogger(FTPSClientFactory.class);

    private FTPClientProperties config;

    public FTPSClientFactory(FTPClientProperties config) {
        this.config = config;
    }

    /**
     * 创建FtpClient对象
     */
    @Override
    public FTPSClient create() {
        FTPSClient ftpsClient = new FTPSClient(false);
        try {
            ftpsClient.connect(config.getHost(), config.getPort());
            ftpsClient.execPBSZ(0);
            ftpsClient.execPROT("P");
            if (!ftpsClient.login(config.getUsername(), config.getPassword())) {
                logger.warn("ftpsClient login failed... username is {}; password: {}", config.getUsername(), config.getPassword());
            }
            ftpsClient.enterLocalPassiveMode();
            ftpsClient.setFileType(FTP.ASCII_FILE_TYPE);
            ftpsClient.setDefaultTimeout(config.getDefaultTimeout());
            ftpsClient.setConnectTimeout(config.getConnectTimeout());
            ftpsClient.setDataTimeout(config.getDataTimeout());
            ftpsClient.setControlEncoding(config.getEncoding());
            int reply = ftpsClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpsClient.disconnect();
                System.out.println("FTPS server refused connection.");
            } else {
                System.out.println("FTPS server connection.");
            }

            ftpsClient.setSoTimeout(config.getSoTimeout());
            ftpsClient.setControlKeepAliveTimeout(config.getControlKeepAliveTimeout());
            ftpsClient.setControlKeepAliveReplyTimeout(config.getControlKeepAliveReplyTimeout());
            ftpsClient.setRemoteVerificationEnabled(false);
            ftpsClient.setBufferSize(config.getBufferSize());
        } catch (IOException e){
            logger.error("create ftps connection failed...", e);
        }
        return ftpsClient;
    }

    /**
     * 用PooledObject封装对象放入池中
     */
    @Override
    public PooledObject<FTPSClient> wrap(FTPSClient ftpClient) {
        return new DefaultPooledObject<>(ftpClient);
    }

    /**
     * 销毁FtpClient对象
     */
    @Override
    public void destroyObject(PooledObject<FTPSClient> ftpPooled) {
        if (ftpPooled == null) {
            return;
        }
        FTPSClient ftpClient = ftpPooled.getObject();
        try {
            ftpClient.logout();
        } catch (IOException io) {
            logger.error("ftp client logout failed...{}", io);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (IOException io) {
                logger.error("close ftps client failed...{}", io);
            }
        }
    }

    public void destroyObject(FTPSClient ftpClient) {
        if (ftpClient == null) {
            return;
        }
        try {
            ftpClient.logout();
        } catch (IOException io) {
            logger.error("ftps client logout failed...{}", io);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (IOException io) {
                logger.error("close ftps client failed...{}", io);
            }
        }
    }

    /**
     * 验证FtpClient对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPSClient> ftpPooled) {
        logger.debug("validate ftpsclient begin");
        try {
            FTPSClient ftpClient = ftpPooled.getObject();
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            logger.error("failed to validate client: {}", e);
        }
        logger.debug("validate ftpsclient end");
        return false;
    }
}
