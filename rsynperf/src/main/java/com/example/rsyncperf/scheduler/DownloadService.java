package com.example.rsyncperf.scheduler;

import com.example.rsyncperf.rsync.BaseResponse;
import com.example.rsyncperf.rsync.RsyncUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class DownloadService implements ApplicationRunner{

    Logger logger = LoggerFactory.getLogger(DownloadService.class);
    @Value("${FTPClientPoolEnable:1}")
    private boolean ftpclientPoolEnable;
    @Value("${FileCompressEnable:1}")
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
        BaseResponse baseResponse = RsyncUtil.exist(rsyncHost,rsyncUsername,rsyncPassword,isEncrypt,rsnycModel,remotePath);
        List<String> fileList = new ArrayList<>();
        if (BaseResponse.isSuccess(baseResponse)){
            String out = (String)baseResponse.getData();
            String[] filePaths = out.split(System.getProperty("line.separator"));
            for (String line:filePaths){
                String[] tmp = line.split(" ");
                if (null!=tmp){
                    fileList.add(tmp[tmp.length-1]);
                }
            }
        }
        return fileList;
    }

    public void download(List<String> fileList) {
        for (String file:fileList){
            if (file.contains("log")||file.contains("txt")){
                RsyncUtil.syncFromRemote(rsyncHost,rsyncUsername,rsyncPassword,isEncrypt,rsnycModel,remotePath+File.separator+file,localPath,false,false,"");
            }
        }
    }


}

