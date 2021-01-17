package ftp;

import org.apache.commons.net.ftp.FTPClient;

public interface FTPClientManager {

    FTPClient borrowObject() throws Exception;

    FTPClient borrowObject(String userName, String password) throws Exception;

    void returnObject(FTPClient obj);

}
