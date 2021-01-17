package ftp;

import org.apache.commons.lang.StringUtils;
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
public class FTPClientFactory extends BasePooledObjectFactory<FTPClient> {
    private static final Logger logger = LoggerFactory.getLogger(FTPClientFactory.class);

    private FTPClientProperties config;

    public FTPClientFactory(FTPClientProperties config) {
        this.config = config;
    }

    /**
     * 创建FtpClient对象
     */
    @Override
    public FTPClient create() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setDefaultTimeout(config.getDefaultTimeout());
        ftpClient.setConnectTimeout(config.getConnectTimeout());
        ftpClient.setDataTimeout(config.getDataTimeout());
        ftpClient.setControlEncoding(config.getEncoding());
        try {
            ftpClient.connect(config.getHost(), config.getPort());
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                logger.warn("ftpServer refused connection,replyCode:{}", replyCode);
                return null;
            }
            if (!ftpClient.login(config.getUsername(), config.getPassword())) {
                logger.warn("ftpClient login failed... username is {}; password: {}", config.getUsername(), config.getPassword());
            }

            ftpClient.setSoTimeout(config.getSoTimeout());
            ftpClient.setControlKeepAliveTimeout(config.getControlKeepAliveTimeout());
            ftpClient.setControlKeepAliveReplyTimeout(config.getControlKeepAliveReplyTimeout());
            ftpClient.setRemoteVerificationEnabled(false);
            ftpClient.setBufferSize(config.getBufferSize());
            ftpClient.setFileType(config.getTransferFileType());
            if (config.isPassiveMode()) {
                ftpClient.enterLocalPassiveMode();
            }
        } catch (IOException e) {
            logger.error("create ftp connection failed...", e);
        }
        return ftpClient;
    }

    public FTPClient create(String userName, String password) {
        if (StringUtils.isEmpty(userName)) {
            userName = config.getUsername();
        }
        if (StringUtils.isEmpty(password)) {
            password = config.getPassword();
        }
        FTPClient ftpClient = new FTPClient();
        ftpClient.setDefaultTimeout(config.getDefaultTimeout());
        ftpClient.setConnectTimeout(config.getConnectTimeout());
        ftpClient.setDataTimeout(config.getDataTimeout());
        ftpClient.setControlEncoding(config.getEncoding());
        try {
            ftpClient.connect(config.getHost(), config.getPort());
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                logger.warn("ftpServer refused connection,replyCode:{}", replyCode);
                return null;
            }
            if (!ftpClient.login(userName, password)) {
                logger.warn("ftpClient login failed... username is {}; password: {}", userName, password);
            }

            ftpClient.setSoTimeout(config.getSoTimeout());
            ftpClient.setControlKeepAliveTimeout(config.getControlKeepAliveTimeout());
            ftpClient.setControlKeepAliveReplyTimeout(config.getControlKeepAliveReplyTimeout());
            ftpClient.setRemoteVerificationEnabled(false);
            ftpClient.setBufferSize(config.getBufferSize());
            ftpClient.setFileType(config.getTransferFileType());
            if (config.isPassiveMode()) {
                ftpClient.enterLocalPassiveMode();
            }
        } catch (IOException e) {
            logger.error("create ftp connection failed...", e);
        }
        return ftpClient;
    }

    /**
     * 用PooledObject封装对象放入池中
     */
    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<>(ftpClient);
    }

    /**
     * 销毁FtpClient对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> ftpPooled) {
        if (ftpPooled == null) {
            return;
        }
        FTPClient ftpClient = ftpPooled.getObject();
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
                logger.error("close ftp client failed...{}", io);
            }
        }
    }

    public void destroyObject(FTPClient ftpClient) {
        if (ftpClient == null) {
            return;
        }
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
                logger.error("close ftp client failed...{}", io);
            }
        }
    }

    /**
     * 验证FtpClient对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> ftpPooled) {
        logger.info("validate ftpclient");
        try {
            FTPClient ftpClient = ftpPooled.getObject();
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            logger.error("failed to validate client: {}", e);
        }
        return false;
    }
}
