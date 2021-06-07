package org.example.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * FTPClient工厂类，通过FTPClient工厂提供FTPClient实例的创建和销毁
 */
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {
    private static final Logger logger= LoggerFactory.getLogger(FtpClientFactory.class);

    private FtpClientProperties config;

    public FtpClientFactory(FtpClientProperties config) {
        this.config = config;
    }

    /**
     * 创建FtpClient对象
     */
    @Override
    public FTPClient create() {
        FTPClient ftpclient = new FTPClient();
        ftpclient.setDefaultTimeout(config.getDefaultTimeout());
        ftpclient.setConnectTimeout(config.getConnectTimeout());
        ftpclient.setDataTimeout(config.getDataTimeout());
        ftpclient.setControlEncoding(config.getEncoding());
        try {
            ftpclient.connect(config.getFtphost(), config.getPort());
            int replyCode = ftpclient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpclient.disconnect();
                logger.warn("ftpServer refused connection,replyCode:{}", replyCode);
                return null;
            }
            if (!ftpclient.login(config.getFtpusername(), config.getFtppassword())) {
                logger.warn("ftpclient login failed... username is {}; password: {}", config.getFtpusername(), config.getFtppassword());
            }

            ftpclient.setSoTimeout(config.getSoTimeout());
            ftpclient.setControlKeepAliveTimeout(config.getControlKeepAliveTimeout());
            ftpclient.setControlKeepAliveReplyTimeout(config.getControlKeepAliveReplyTimeout());
            ftpclient.setRemoteVerificationEnabled(false);
            ftpclient.setBufferSize(config.getBufferSize());
            ftpclient.setFileType(config.getFileType());
            if (config.isPassiveMode()) {
                ftpclient.enterLocalPassiveMode();
            }
        } catch (IOException e) {
            logger.error("create ftp connection failed...", e);
        }
        return ftpclient;
    }

    /**
     * 用PooledObject封装对象放入池中
     */
    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpclient) {
        return new DefaultPooledObject<>(ftpclient);
    }

    /**
     * 销毁FtpClient对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> ftpPooled) {
        if (ftpPooled == null) {
            return;
        }
        FTPClient ftpclient = ftpPooled.getObject();
        try {
            ftpclient.logout();
        } catch (IOException io) {
            logger.error("ftp client logout failed...{}", io);
        } finally {
            try {
                if (ftpclient.isConnected()) {
                    ftpclient.disconnect();
                }
            } catch (IOException io) {
                logger.error("close ftp client failed...{}", io);
            }
        }
    }

    public void destroyObject(FTPClient ftpclient) {
        if (ftpclient == null) {
            return;
        }
        try {
            ftpclient.logout();
        } catch (IOException io) {
            logger.error("ftp client logout failed...{}", io);
        } finally {
            try {
                if (ftpclient.isConnected()) {
                    ftpclient.disconnect();
                }
            } catch (IOException io) {
                logger.error("close ftp client failed...{}", io);
            }
        }
    }

    /**
     * 验证FtpClient对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> ftpPooled) {
        logger.debug("validate ftpclient begin");
        try {
            FTPClient ftpclient = ftpPooled.getObject();
            return ftpclient.sendNoOp();
        } catch (IOException e) {
            logger.error("failed to validate client: {}", e);
        }
        logger.debug("validate ftpclient end");
        return false;
    }
}
