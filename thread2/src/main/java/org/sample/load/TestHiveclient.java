package org.sample.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class TestHiveclient {
    private static final Logger logger = LoggerFactory.getLogger(TestHiveclient.class);

    public void load (String[] cmd) throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmd);
        String infoStr;
        String errorStr;
        String infoLog = "";
        String errorLog = "";
        String successCount = null;
        String failCount = null;
        try {
            BufferedReader infoReader;
            BufferedReader errorReader;
            infoReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            //日志取样行数。
            int sampling = 1000;
            //加载异常(如出现脏数据)且值降为0时 ，kill process。
            int killProcessWhenZero = 5;
            //组装info日志,一次性输出（组装的目的：防止多线程应用日志太过分散）
            while ((infoStr = infoReader.readLine()) != null) {
                if (sampling > 0) {
                    infoLog = infoLog + "\n" + infoStr;
                    //考虑到日志文件巨大，不全部输出；输出1000行用于排错足够（每行数据报错信息相似）
                    sampling--;
                }
            }

            int result = process.waitFor();
            if (result == 0) {
                logger.info(infoLog);
                logger.info(successCount);
                logger.info(failCount);
                logger.info("end to load sucess");
            } else {
                logger.info(infoLog);
                logger.info(successCount);
                logger.info(failCount);
                logger.error("end to load fail");
            }

        } catch (Exception e){
            logger.error("",e);
        }


    }

    public static void main(String[] args) throws InterruptedException {

        String[] cmdarray = new String[3];
        String source = "source ~/.bash_profile && ";
        cmdarray[0] = "/bin/sh";
        cmdarray[1] = "-c";
        cmdarray[2] =source +  " hive -e  \" load data local inpath '/data/dfs01/qingqinz/wangchunyu001-ZZHJQKMB-20200131.txt' into table checkorgdev.T_KJ_ZZQKM partition (load_date='20201205',org_no='ttc20200731')\"";

        TestHiveclient testDatax = new TestHiveclient();

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    testDatax.load(cmdarray);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        thread.join(100000);
        logger.info("date1:{}",new Date());




    }




}
