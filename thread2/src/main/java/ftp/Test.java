package ftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception {
//        FTPClientUtil ftpClientUtil = FTPClientUtil.getInstance();
//        try {
//            ftpClientUtil.download("j1234567890/20200713/j1234567890-JGXXB-20200713.txt","/Users/zqq/Downloads/zhong/ftpclienthome/j1234567890/20200713/j1234567890-JGXXB-20200713.txt");
//        } catch (Exception e) {
//            logger.error("",e);
//        }


//        FtpClientProperties ftpClientProperties = new FtpClientProperties();
//        FtpClientFactory ftpClientFactory = new FtpClientFactory(ftpClientProperties);
//        GenericObjectPool<FTPClient> ftpClientPool = new GenericObjectPool<>(ftpClientFactory);
//        while(true){
//            FTPClient ftpClient;
//            ftpClient = ftpClientPool.borrowObject();
//
//            System.out.println(ftpClient.getReplyCode());
//            ftpClientPool.returnObject(ftpClient);
//        }

        FTPClientUtil ftpClientUtil = FTPClientUtil.getInstance(false);
        int i = 0;
//        ftpClientUtil.download("/center/wangchunyu001-DGXDYWJJ-20200807.txt","/Users/zqq/Downloads/bankfilepath/ftptest/wangchunyu001-DGXDYWJJ-20200807.txt","ftpgz","123");
//        ftpClientUtil.upload("/Users/zqq/Downloads/bankfilepath/ftptest/","wangchunyu001-DGXDYWJJ-20200807.txt","20200101/111");
        ftpClientUtil.delete("20200101/111", "wangchunyu001-DGXDYWJJ-20200807.txt");

        if (i > 10) {
            Thread.sleep(1000 * 60 * 7);
        }

    }
}
