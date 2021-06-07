package com.example.rsyncperf.scheduler;

import com.example.rsyncperf.rsync.BaseResponse;
import com.example.rsyncperf.rsync.RsyncUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ScanService {

   Logger logger = LoggerFactory.getLogger(ScanService.class);

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

   public void scan() throws Exception {
         RsyncUtil.exist(rsyncHost,rsyncUsername,rsyncPassword,isEncrypt,rsnycModel,remotePath);
//      RsyncUtil.existByShell(rsyncHost,rsyncUsername,rsyncPassword,isEncrypt,rsnycModel,"titic20200731001/20210217/ADttc2020073120210217");
//      BaseResponse baseResponse = RsyncUtil.syncFromRemote(rsyncHost,rsyncUsername,rsyncPassword,isEncrypt,rsnycModel,"titic20200731001/20210217/ADttc2020073120210217/titic20200731001-DGXDFHZMXJL-20210217.log", "/Users/zqq/Downloads/bankfilepath/rsynctest",false,false,"");
   }


}
