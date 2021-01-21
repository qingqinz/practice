package org.sample.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class TestDatax {
    private static final Logger logger = LoggerFactory.getLogger(TestDatax.class);

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
//            int` result = process.waitFor();
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
                } else {//超过取样日志后，只记录关键信息
                    if (infoStr.startsWith("任务") || infoStr.startsWith("记录") || infoStr.startsWith("读")) {
                        infoLog = infoLog + "\n" + infoStr;
                    }
                }
                //获取成功记录和失败记录
                if (infoStr.startsWith("读出记录总数")) {
                    successCount = infoStr;
                } else if (infoStr.startsWith("读写失败总数")) {
                    failCount = infoStr;
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

        String dataxBasePath = "/Users/zqq/Downloads/datax-mini/";
        //对执行命令的参数进行补充和完善(补充不从properties配置文件或DB获取的参数）
        String argsStr = "-DhiveUserName=root -DhiveJdbcUrl=jdbc:hive2://172.16.10.154:10000/checkorgdev -DhivePassword=zj2018  ";
        String ftpFileToLoad ="/Users/zqq/Downloads/bankfilepath/scan/titic20200731001/20201101/titic20200731001-GRHQCKFHZ-20201101.txt";
        String hdfsFileName =  "T_KJ_GRHCFZ";
        String orgNo="ttc20200731";
        String loadDate="20201205";
        String args1 = argsStr + " -Dftpfilepath=" + ftpFileToLoad + " -Dhdfsfilename="
                + hdfsFileName + " -DorgNo=" + orgNo + " -DloadDate=" + loadDate;
        //最终被执行的命令（重要）
        String[] cmdarray = new String[5];
        cmdarray[0] = "python";
        cmdarray[1] = dataxBasePath + "/bin/datax.py";
        cmdarray[2] = "-p";
        cmdarray[3] = args1;
        cmdarray[4] = dataxBasePath + "job/txt2hivestrict/GRHQCKFHZ.json";

        TestDatax testDatax = new TestDatax();

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
