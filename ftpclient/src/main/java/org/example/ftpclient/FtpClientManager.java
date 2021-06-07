package org.example.ftpclient;

import org.apache.commons.net.ftp.FTPClient;

public interface FtpClientManager {

    FTPClient borrowObject() throws Exception;
    void returnObject(FTPClient obj);

}
