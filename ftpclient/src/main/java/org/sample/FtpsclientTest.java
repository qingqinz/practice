package org.sample;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FtpsclientTest {
    private static final Logger logger= LoggerFactory.getLogger(FtpsclientTest.class);

    public static void main(String[] args) throws Exception {

        String remote = "ftps";
        ArrayList<String> strings = new ArrayList<>();
        strings.add("/data/dfs01/qingqinz/javaprogram/ftpsclient/ten/wangchunyu001-DGXDYWJJ-20200807.txt");
        strings.add("/data/dfs01/qingqinz/javaprogram/ftpsclient/twenty/wangchunyu001-DGXDYWJJ-20200807.txt");
        strings.add("/data/dfs01/qingqinz/javaprogram/ftpsclient/forty/wangchunyu001-DGXDYWJJ-20200807.txt");
        strings.add("/data/dfs01/qingqinz/javaprogram/ftpsclient/eity/wangchunyu001-DGXDYWJJ-20200807.txt");

//        strings.add("/Users/zqq/Downloads/bankfilepath/ftptest/ten/wangchunyu001-DGXDYWJJ-20200807.txt");
//        strings.add("/Users/zqq/Downloads/bankfilepath/ftptest/twenty/wangchunyu001-DGXDYWJJ-20200807.txt");
//        strings.add("/Users/zqq/Downloads/bankfilepath/ftptest/forty/wangchunyu001-DGXDYWJJ-20200807.txt");
//        strings.add("/Users/zqq/Downloads/bankfilepath/ftptest/eity/wangchunyu001-DGXDYWJJ-20200807.txt");
        InputStream input;
        int i =0;
        for (String str:strings){
            remote = remote + i++;

            FTPSClient ftpsClient = new FTPSClient(false);
            ftpsClient.connect("172.16.10.35",21);//FTP的地址与端口
            ftpsClient.execPBSZ(0);
            ftpsClient.execPROT("P");
            ftpsClient.login("wangchunyu001","123");//FTP的用户名和密码
            ftpsClient.enterLocalPassiveMode();
//        ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpsClient.setFileType(FTP.ASCII_FILE_TYPE);
            ftpsClient.setConnectTimeout(30000);
            ftpsClient.setBufferSize(1024*1024*5);
            ftpsClient.setDataTimeout(60000);
            ftpsClient.setControlEncoding("UTF-8");
            int reply = ftpsClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpsClient.disconnect();
                System.out.println("FTP server refused connection.");
            } else {
                System.out.println("FTP server connection.");
            }
            input = new FileInputStream(str);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            Date date1 = new Date();
            logger.info(remote + "begin {}",simpleDateFormat.format(date1));
            boolean bool = ftpsClient.storeFile(remote, input);
            if (bool){
                Date date2 = new Date();
                logger.info(remote + "end {}",simpleDateFormat.format(date2));
                logger.info("diff:{}",(double)(date2.getTime()-date1.getTime())/1000/60);
            }
            input.close();
            ftpsClient.logout();
        }

    }
}
