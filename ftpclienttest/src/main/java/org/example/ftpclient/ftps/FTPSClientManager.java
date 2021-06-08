package org.example.ftpclient.ftps;

import org.apache.commons.net.ftp.FTPSClient;

public interface FTPSClientManager {

    FTPSClient borrowObject() throws Exception;
    void returnObject(FTPSClient obj);

}
