package org.example.ftpclient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class FtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    protected FtpClientManager ftpclientManager;

    public boolean delete(String path) throws Exception{
        boolean result=true;
        FTPClient ftpclient = new FTPClient();
        String fileName = getFileName(path);
        try {
            ftpclient = ftpclientManager.borrowObject();
            int replyCode = ftpclient.getReplyCode();
            logger.debug("@ftpclient@ replyCode5:{}", replyCode);
            String[] files = ftpclient.listNames(path);
            for (String file : files) {
                logger.debug("@ftpclient@ begin delete file {}", file);
                result = ftpclient.deleteFile(file);
                logger.debug("@ftpclient@ delete file result,{},{}", file, result);
            }
        } catch (Exception e){
            logger.error("@ftpclient@ delete file exception,{}",fileName,e);
            throw e;
        } finally {
            ftpclientManager.returnObject(ftpclient);
        }
        return result;
    }

    private String getFileName(String fileName){
        return fileName.substring(fileName.lastIndexOf('/')+1);
    }

    public boolean download(String remotePath,String localPath) throws Exception{
        boolean result = false;
        FTPClient ftpclient = new FTPClient();
        FileOutputStream fileOutputStream=null;
        try {
            ftpclient = ftpclientManager.borrowObject();
            int replyCode = ftpclient.getReplyCode();
            logger.debug("@ftpclient@ replyCode0:{}", replyCode);
            File file = new File(localPath);
            fileOutputStream = new FileOutputStream(file);
            if (null != fileOutputStream) {
                logger.debug("@ftpclient@ begin download file {}",remotePath);
                result = ftpclient.retrieveFile(remotePath, fileOutputStream);
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
            ftpclientManager.returnObject(ftpclient);
        }
        return result;
    }

    public boolean mdmtFile(String remote) throws Exception {
        boolean result=false;
        FTPClient ftpclient = new FTPClient();
        try {
            ftpclient = ftpclientManager.borrowObject();
            int replyCode = ftpclient.getReplyCode();
            logger.debug("@ftpclient@ replyCode1:{}", replyCode);
            FTPFile ftpFile = ftpclient.mdtmFile(remote);
            replyCode = ftpclient.getReplyCode();
            logger.debug("@ftpclient@ replyCode2:{}", replyCode);
            if (null!=ftpFile){
                result=true;
            }
            logger.debug("@ftpclient@ mdtmFile result:{},{}",remote,result);
        } catch (Exception e){
            logger.error("@ftpclient@ judge file exist exception,{}",remote,e);
            throw e;
        } finally {
            ftpclientManager.returnObject(ftpclient);
        }
        return result;
    }

    public boolean listFile(String remote) throws Exception {
        boolean result=false;
        FTPClient ftpclient = new FTPClient();
        try {
            ftpclient = ftpclientManager.borrowObject();
            int replyCode = ftpclient.getReplyCode();
            logger.debug("@ftpclient@ replyCode3:{}", replyCode);
            FTPFile[] ftpFile = ftpclient.listFiles(remote);
            replyCode = ftpclient.getReplyCode();
            logger.debug("@ftpclient@ replyCode4:{}", replyCode);
            if (ftpFile.length!=0){
                result=true;
            }
            logger.debug("@ftpclient@ listFiles result:{},{}",remote,result);
        } catch (Exception e){
            logger.error("@ftpclient@ judge directory exist exception,{}",remote,e);
            throw e;
        } finally {
            ftpclientManager.returnObject(ftpclient);
        }
        return result;
    }


    public List<String> listFile(String remote, String prefix) throws Exception {
        boolean result=false;
        List<String> files = new ArrayList<>();
        FTPClient ftpclient = new FTPClient();
        try {
            ftpclient = ftpclientManager.borrowObject();
            int replyCode = ftpclient.getReplyCode();
            logger.info("@ftpclient@ replyCode6:{}", replyCode);
            FTPFile[] ftpFile = new FTPFile[]{};
            if (StringUtils.isEmpty(prefix)){
                ftpFile = ftpclient.listFiles(remote);
            }else{
                ftpFile=ftpclient.listFiles(remote, new FTPFileFilter() {
                    @Override
                    public boolean accept(FTPFile file) {
                        return file.getName().startsWith(prefix);
                    }
                });
            }
            replyCode = ftpclient.getReplyCode();
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
            ftpclientManager.returnObject(ftpclient);
        }
        return files;
    }

    public boolean upload(String localPath, String fileName, String remotePath) throws Exception {
        boolean result = false;
        if (null==remotePath){
            logger.error("remotePath can not be null");
            return false;
        }
        FTPClient ftpclient = new FTPClient();
        FileInputStream fileInputStream = null;
        localPath=localPath+"/"+fileName;

        /*
        判断目标目录不存在
         */
        String[] dirs= remotePath.split("/");
        try {
            ftpclient = ftpclientManager.borrowObject();
            String tmp="";
            for (String dir:dirs){
                if (!StringUtils.isEmpty(tmp)){
                    if (!StringUtils.isEmpty(dir)){
                        tmp=tmp+"/"+dir;
                        ftpclient.makeDirectory(tmp);
                    }
                } else {
                    if (!StringUtils.isEmpty(dir)){
                        tmp=dir;
                        ftpclient.makeDirectory(tmp);
                    }
                }
            }
            int replyCode = ftpclient.getReplyCode();
            logger.debug("@ftpclient@ replyCode4:{}", replyCode);
            File file = new File(localPath);
            if (!(file.exists() && file.isFile())) {
                logger.info("@ftpclient@ local file does not exist,{} ", fileName);
                return false;
            }
            fileInputStream = new FileInputStream(file);
            if (null != fileInputStream) {
                String finalPath = StringUtils.isEmpty(remotePath)? fileName:remotePath + "/" + fileName;
                if (null!=ftpclient.mdtmFile(finalPath)){
                    logger.debug("@ftpclient@ begin delete old file {}", fileName);
                    result = ftpclient.deleteFile(finalPath);
                    logger.debug("@ftpclient@ delete old file result,{},{}", fileName, result);
                }
                logger.debug("@ftpclient@ begin upload file {}", fileName);
                result = ftpclient.storeFile(finalPath, fileInputStream);
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
            if (null!=ftpclient){
                ftpclientManager.returnObject(ftpclient);
            }
        }
        return result;
    }

    public boolean delete(String remotePath, String fileName) throws Exception {
        boolean result = true;
        FTPClient ftpclient = new FTPClient();
        String serverFilePath = remotePath + "/" + fileName;
        try {
            ftpclient = ftpclientManager.borrowObject();
            int replyCode = ftpclient.getReplyCode();
            logger.debug("@ftpclient@ replyCode5:{}", replyCode);
            if (null != ftpclient.mdtmFile(serverFilePath)) {
                logger.debug("@ftpclient@ begin delete file {}", serverFilePath);
                result = ftpclient.deleteFile(serverFilePath);
                logger.debug("@ftpclient@ delete file result,{},{}", serverFilePath, result);
            }
        } finally {
            ftpclientManager.returnObject(ftpclient);
        }
        return result;
    }

    public boolean deleteDirectory(String serverDirectoryPath) throws Exception {
        boolean result = true;
        FTPClient ftpclient = new FTPClient();
        try {
            ftpclient = ftpclientManager.borrowObject();
            String[] files = ftpclient.listNames(serverDirectoryPath);
            for (String file : files) {
                try {
                    logger.info("@ftpclient@ begin delete file {}", file);
                    result = ftpclient.deleteFile(file);
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
            ftpclientManager.returnObject(ftpclient);
        }
        return result;
    }
}
