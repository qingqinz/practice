package org.example.ftpclient.ftp;

import org.apache.commons.net.ftp.FTP;
import org.example.ftpclient.util.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class FTPClientProperties {
    private static final Logger logger= LoggerFactory.getLogger(FTPClientProperties.class);

    private static Environment env;

    /**
     * ftp地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port = 21;

    /**
     * 登录用户
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 被动模式
     */
    private boolean passiveMode = true;

    /**
     * 编码
     */
    private String encoding = "UTF-8";

    /**
     * 连接超时时间(秒)
     */
    private Integer connectTimeout;

    /**
     * 传输超时时间(秒)
     */
    private Integer dataTimeout;

    /**
     * 缓冲大小
     */
    private Integer bufferSize = 1024;

    /**
     * 设置keepAlive
     * 单位:秒  0禁用
     * Zero (or less) disables
     */
    private Integer controlKeepAliveTimeout = 0;
    private Integer defaultTimeout = 0;
    private Integer soTimeout = 0;
    private Integer controlKeepAliveReplyTimeout = 0;

    /**
     * 传输文件类型
     * in theory this should not be necessary as servers should default to ASCII
     * but they don't all do so - see NET-500
     */
    private Integer transferFileType = FTP.ASCII_FILE_TYPE;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getDataTimeout() {
        return dataTimeout;
    }

    public void setDataTimeout(Integer dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Integer getControlKeepAliveTimeout() {
        return controlKeepAliveTimeout;
    }

    public void setControlKeepAliveTimeout(Integer controlKeepAliveTimeout) {
        this.controlKeepAliveTimeout = controlKeepAliveTimeout;
    }

    public Integer getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(Integer defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public Integer getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(Integer soTimeout) {
        this.soTimeout = soTimeout;
    }

    public Integer getControlKeepAliveReplyTimeout() {
        return controlKeepAliveReplyTimeout;
    }

    public void setControlKeepAliveReplyTimeout(Integer controlKeepAliveReplyTimeout) {
        this.controlKeepAliveReplyTimeout = controlKeepAliveReplyTimeout;
    }

    public Integer getTransferFileType() {
        return transferFileType;
    }

    public void setTransferFileType(Integer transferFileType) {
        this.transferFileType = transferFileType;
    }

    static{
        env = ApplicationContextProvider.getBean(Environment.class);
    }

    public FTPClientProperties(){

        /*
        local test
         */
//        host = "172.16.10.171";
//        username = "center";
//        password = "center";
//        defaultTimeout = 300000;
//        connectTimeout = 60000;
//        dataTimeout = 300000;
//        soTimeout = 300000;
//        controlKeepAliveTimeout = 60;
//        controlKeepAliveReplyTimeout = 6000;
//        bufferSize = 10240000;
//        transferFileType = 2;

        host = env.getProperty("ftphost");
        username = env.getProperty("ftpusername");
        password = env.getProperty("ftppassword");
        defaultTimeout = Integer.valueOf(env.getProperty("ftpclient.DefaultTimeout"));
        connectTimeout = Integer.valueOf(env.getProperty("ftpclient.ConnectTimeout"));
        dataTimeout = Integer.valueOf(env.getProperty("ftpclient.DataTimeout"));
        soTimeout = Integer.valueOf(env.getProperty("ftpclient.SoTimeout"));
        controlKeepAliveTimeout = Integer.valueOf(env.getProperty("ftpclient.ControlKeepAliveTimeout"));
        controlKeepAliveReplyTimeout = Integer.valueOf(env.getProperty("ftpclient.ControlKeepAliveReplyTimeout"));
        bufferSize = Integer.valueOf(env.getProperty("ftpclient.BufferSize"));
        transferFileType = Integer.valueOf(env.getProperty("ftpclient.FileType"));

    }


}
