package org.example.ftpclient.ftps;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.example.ftpclient.util.AbstractFtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class FTPSUtil extends AbstractFtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPSUtil.class);

    protected FTPSClientManager ftpClientManager;

    public boolean delete(String path) throws Exception{
        boolean result=true;
        FTPSClient ftpClient = new FTPSClient();
        String fileName = getFileName(path);
        try {
            ftpClient = ftpClientManager.borrowObject();
            int replyCode = ftpClient.getReplyCode();
            logger.debug("@ftpclient@ replyCode5:{}", replyCode);
            String[] files = ftpClient.listNames(path);
            for (String file : files) {
                logger.debug("@ftpclient@ begin delete file {}", file);
                result = ftpClient.deleteFile(file);
                logger.debug("@ftpclient@ delete file result,{},{}", file, result);
            }
        } catch (Exception e){
            logger.error("@ftpclient@ delete file exception,{}",fileName,e);
            throw e;
        } finally {
            ftpClientManager.returnObject(ftpClient);
        }
        return result;
    }

    private String getFileName(String fileName){
        return fileName.substring(fileName.lastIndexOf('/')+1);
    }

    public boolean download(String remotePath,String localPath) throws Exception{
        boolean result = false;
        FTPSClient ftpClient = new FTPSClient();
        FileOutputStream fileOutputStream=null;
        try {
            ftpClient = ftpClientManager.borrowObject();
            int replyCode = ftpClient.getReplyCode();
            logger.debug("@ftpclient@ replyCode0:{}", replyCode);
            File file = new File(localPath);
            fileOutputStream = new FileOutputStream(file);
            if (null != fileOutputStream) {
                logger.debug("@ftpclient@ begin download file {}",remotePath);
                result = ftpClient.retrieveFile(remotePath, fileOutputStream);
            }
            if (!result){
                logger.error("@ftpclient@ download file result is false,{}", remotePath);
            } else {
                logger.debug("@ftpclient@ download file result is true,{}", remotePath);
            }
        } catch (Exception e){
            logger.error("@ftpclient@ download file exception,{}",remotePath,e);
            throw e;
        } finally {
            try {
                if (null!=fileOutputStream){
                    fileOutputStream.close();
                }
            } catch (Exception e){
                logger.error("@ftpclient@ close fileOutputStream exception",e);
            }
            ftpClientManager.returnObject(ftpClient);
        }
        return result;
    }

    public boolean mdmtFile(String remote) throws Exception {
        boolean result=false;
        FTPSClient ftpClient = new FTPSClient();
        try {
            ftpClient = ftpClientManager.borrowObject();
            int replyCode = ftpClient.getReplyCode();
            logger.debug("@ftpclient@ replyCode1:{}", replyCode);
            FTPFile ftpFile = ftpClient.mdtmFile(remote);
            replyCode = ftpClient.getReplyCode();
            logger.debug("@ftpclient@ replyCode2:{}", replyCode);
            if (null!=ftpFile){
                result=true;
            }
            logger.debug("@ftpclient@ mdtmFile result:{},{}",remote,result);
        } catch (Exception e){
            logger.error("@ftpclient@ judge file exist exception,{}",remote,e);
            throw e;
        } finally {
            ftpClientManager.returnObject(ftpClient);
        }
        return result;
    }

    public boolean listFile(String remote) throws Exception {
        boolean result=false;
        FTPSClient ftpClient = new FTPSClient();
        try {
            ftpClient = ftpClientManager.borrowObject();
            int replyCode = ftpClient.getReplyCode();
            logger.debug("@ftpclient@ replyCode3:{}", replyCode);
            FTPFile[] ftpFile = ftpClient.listFiles(remote);
            replyCode = ftpClient.getReplyCode();
            logger.debug("@ftpclient@ replyCode4:{}", replyCode);
            if (ftpFile.length!=0){
                result=true;
            }
            logger.debug("@ftpclient@ listFiles result:{},{}",remote,result);
        } catch (Exception e){
            logger.error("@ftpclient@ judge directory exist exception,{}",remote,e);
            throw e;
        } finally {
            ftpClientManager.returnObject(ftpClient);
        }
        return result;
    }


    public List<String> listFile(String remote, String prefix) throws Exception {
        boolean result=false;
        List<String> files = new ArrayList<>();
        FTPSClient ftpClient = new FTPSClient();
        try {
            ftpClient = ftpClientManager.borrowObject();
            int replyCode = ftpClient.getReplyCode();
            logger.info("@ftpclient@ replyCode6:{}", replyCode);
            FTPFile[] ftpFile = new FTPFile[]{};
            if (StringUtils.isEmpty(prefix)){
                ftpFile = ftpClient.listFiles(remote);
            }else{
                ftpFile=ftpClient.listFiles(remote, new FTPFileFilter() {
                    @Override
                    public boolean accept(FTPFile file) {
                        return file.getName().startsWith(prefix);
                    }
                });
            }
            replyCode = ftpClient.getReplyCode();
            logger.info("@ftpclient@ replyCode7:{}", replyCode);
            if (ftpFile.length!=0){
                result=true;
            }
            for (FTPFile ftpFile1:ftpFile){
                files.add(ftpFile1.getName());
            }
            logger.info("@ftpclient@ listFiles result:{},{}",remote,result);
        } catch (Exception e){
            logger.error("@ftpclient@ judge directory exist exception,{}",remote,e);
            throw e;
        } finally {
            ftpClientManager.returnObject(ftpClient);
        }
        return files;
    }

    public boolean upload(String localPath, String fileName, String remotePath) throws Exception {
        boolean result = false;
        if (null==remotePath){
            logger.error("remotePath can not be null");
            return false;
        }
        FTPSClient ftpClient = new FTPSClient();
        FileInputStream fileInputStream = null;
        localPath=localPath+"/"+fileName;

        /*
        判断目标目录不存在
         */
        String[] dirs= remotePath.split("/");
        try {
            ftpClient = ftpClientManager.borrowObject();
            String tmp="";
            for (String dir:dirs){
                if (!StringUtils.isEmpty(tmp)){
                    if (!StringUtils.isEmpty(dir)){
                        tmp=tmp+"/"+dir;
                        ftpClient.makeDirectory(tmp);
                    }
                } else {
                    if (!StringUtils.isEmpty(dir)){
                        tmp=dir;
                        ftpClient.makeDirectory(tmp);
                    }
                }
            }
            int replyCode = ftpClient.getReplyCode();
            logger.debug("@ftpclient@ replyCode4:{}", replyCode);
            File file = new File(localPath);
            if (!(file.exists() && file.isFile())) {
                logger.info("@ftpclient@ local file does not exist,{} ", fileName);
                return false;
            }
            fileInputStream = new FileInputStream(file);
            if (null != fileInputStream) {
                String finalPath = StringUtils.isEmpty(remotePath)? fileName:remotePath + "/" + fileName;
                if (null!=ftpClient.mdtmFile(finalPath)){
                    logger.debug("@ftpclient@ begin delete old file {}", fileName);
                    result = ftpClient.deleteFile(finalPath);
                    logger.debug("@ftpclient@ delete old file result,{},{}", fileName, result);
                }
                logger.debug("@ftpclient@ begin upload file {}", fileName);
                result = ftpClient.storeFile(finalPath, fileInputStream);
            }
            if (!result){
                logger.error("@ftpclient@ upload file result is false,{}", fileName);
            } else {
                logger.debug("@ftpclient@ upload file result is true,{}", fileName);
            }
        }finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                logger.error("@ftpclient@ close fileinputstream exception", e);
            }
            if (null!=ftpClient){
                ftpClientManager.returnObject(ftpClient);
            }
        }
        return result;
    }




    public boolean delete(String remotePath, String fileName) throws Exception {
        boolean result = true;
        FTPSClient ftpClient = new FTPSClient();
        String serverFilePath = remotePath + "/" + fileName;
        try {
            ftpClient = ftpClientManager.borrowObject();
            int replyCode = ftpClient.getReplyCode();
            logger.debug("@ftpclient@ replyCode5:{}", replyCode);
            if (null != ftpClient.mdtmFile(serverFilePath)) {
                logger.debug("@ftpclient@ begin delete file {}", serverFilePath);
                result = ftpClient.deleteFile(serverFilePath);
                logger.debug("@ftpclient@ delete file result,{},{}", serverFilePath, result);
            }
        } finally {
            ftpClientManager.returnObject(ftpClient);
        }
        return result;
    }

    public boolean deleteDirectory(String serverDirectoryPath) throws Exception {
        boolean result = true;
        FTPSClient ftpClient = new FTPSClient();
        try {
            ftpClient = ftpClientManager.borrowObject();
            String[] files = ftpClient.listNames(serverDirectoryPath);
            for (String file : files) {
                try {
                    logger.info("@ftpclient@ begin delete file {}", file);
                    result = ftpClient.deleteFile(file);
                    logger.info("@ftpclient@ delete file result,{},{}", file, result);
                } catch (Exception e) {
                    result = false;
                    logger.error("@ftpclient@ delete file {} exception,{}", file, e);
                }
            }
        } catch (Exception e) {
            logger.error("@ftpclient@ delete directory {} exception,{}", serverDirectoryPath, e);
            throw e;
        } finally {
            ftpClientManager.returnObject(ftpClient);
        }
        return result;
    }
}
