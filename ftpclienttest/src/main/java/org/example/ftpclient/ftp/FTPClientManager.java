package org.example.ftpclient.ftp;

import org.apache.commons.net.ftp.FTPClient;

public interface FTPClientManager {

    FTPClient borrowObject() throws Exception;
    void returnObject(FTPClient obj);

}
