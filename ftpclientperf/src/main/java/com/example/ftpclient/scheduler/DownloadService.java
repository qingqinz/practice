package com.example.ftpclient.scheduler;

import com.example.ftpclient.ftp.FTPClientWithPoolUtil;
import com.example.ftpclient.ftp.FTPClientWithoutPoolUtil;
import com.example.ftpclient.ftps.FTPSClientWithPoolUtil;
import com.example.ftpclient.ftps.FTPSClientWithoutPoolUtil;
import com.example.ftpclient.util.AbstractFtpUtil;
import com.example.ftpclient.util.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class DownloadService implements ApplicationRunner {

    Logger logger = LoggerFactory.getLogger(DownloadService.class);
    @Value("${SSLEnable}")
    private boolean sslEnable;
    @Value("${FTPClientPoolEnable}")
    private boolean ftpclientPoolEnable;
    @Value("${FileCompressEnable}")
    private boolean fileCompressEnable;
    @Value("${rsync.enable}")
    private boolean rsyncEnable;
    @Value("${rsync.username}")
    private String rsyncUsername;
    @Value("${rsync.password}")
    private String rsyncPassword;
    @Value("${rsync.host}")
    private String rsyncHost;
    @Value("${rsync.isencrypt}")
    private boolean isEncrypt;
    @Value("${rsync.model}")
    private String rsnycModel;
    @Value("${rsync.remote.path}")
    private String remotePath;
    @Value("${rsync.local.path}")
    private String localPath;
    @Value("${rsync.download.times}")
    private int times;
    @Value("${rsync.download.thread.number}")
    private int downloadThreadNumber;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startThread();
    }

    public void startThread() {
        List<String> fileList = getFileList();
        List[] lists = dispatch(fileList,downloadThreadNumber);
        for(int i=0;i<times;i++){
            logger.info("this is {} time ",i);
            CountDownLatch countDownLatch = new CountDownLatch(downloadThreadNumber);
            for (int y=0;y<lists.length;y++){
                new Thread(new Worker(lists[y],countDownLatch,this)).start();
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List[] dispatch(List<String> list, int threadNum) {
        List[] idsDispatched = new ArrayList[threadNum];
        for (int i = 0; i < threadNum; i++) {
            idsDispatched[i] = new ArrayList();
        }
        for (int i = 0; i < list.size(); i++) {
            String string = list.get(i);
            idsDispatched[i % threadNum].add(string);
        }
        return idsDispatched;
    }

    public List<String> getFileList() {

        File file = new File(localPath);
        String[] files= file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("txt")||name.endsWith("log");
            }
        });
        return  Arrays.asList(files);
    }

    public void download(List<String> fileList) {
        for (String file:fileList){
            boolean result=true;
            if (file.endsWith("log")||file.endsWith("txt")){
                String reportZipPathName = localPath +"/" + file+".zip";
                String reportZipName = file+".zip";
                compress(localPath + "/" + file,reportZipPathName);
                if (fileCompressEnable){
                    file = reportZipName;
                }
                AbstractFtpUtil ftpUtil;
                if (ftpclientPoolEnable){
                    if (sslEnable){
                        ftpUtil = FTPSClientWithPoolUtil.getInstance();
                    } else {
                        ftpUtil = FTPClientWithPoolUtil.getInstance();
                    }
                } else {
                    if (sslEnable){
                        ftpUtil =  new FTPSClientWithoutPoolUtil();
                    } else {
                        ftpUtil =  new FTPClientWithoutPoolUtil();
                    }
                }
                try {
                    result = ftpUtil.upload(localPath, file, remotePath);
                } catch (Exception e) {
                    logger.error("上传异常",e);
                }
                if (!result){
                    logger.error("上传失败");
                } else {
                    logger.info("上传成功");
                }
            }
        }
    }

    private boolean compress(String reportPathName, String reportZipPathName) {
        try {
            if (fileCompressEnable) {
                logger.info("开始压缩,{}", reportPathName);
                File zipFile = new File(reportZipPathName);
                if (zipFile.exists()) {
                    logger.info("压缩文件存在不需要压缩,{}",  reportPathName);
                } else {
                    ArrayList<File> files = new ArrayList<>();
                    List<File> fileList = files;
                    fileList.add(new File(reportPathName));
                    FileOutputStream fos2 = new FileOutputStream(new File(reportZipPathName));
                    ZipUtil.toZip(fileList, fos2);
                    logger.info("压缩完成,{}",reportPathName);
                }
            }
        } catch (Exception e) {
            logger.error("压缩异常,{}", reportPathName, e);
            return false;
        }
        return true;
    }


}

