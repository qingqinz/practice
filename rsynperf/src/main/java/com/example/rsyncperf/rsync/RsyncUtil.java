package com.example.rsyncperf.rsync;
/*
 * --verbose, -v            increase verbosity
 * --archive, -a            archive mode is -rlptgoD (no -A,-X,-U,-N,-H)
 * --recursive, -r          recurse into directories
 * --remove-source-files    sender removes synchronized files (non-dir)
 *
 * --compress, -z           compress file data during the transfer
 * --compress-choice=STR    choose the compression algorithm (aka --zc)
 * --compress-level=NUM     explicitly set compression level (aka --zl)
 * --skip-compress=LIST     skip compressing files with suffix in LIST
 *
 * --temp-dir=DIR, -T       create temporary files in directory DIR
 *
 * --backup, -b             make backups (see --suffix & --backup-dir)
 * --backup-dir=DIR         make backups into hierarchy based in DIR
 * --suffix=SUFFIX          backup suffix (default ~ w/o --backup-dir)
 *
 * --mkpath                 create the destination's path component
 *
 * --log-file=FILE          log what we're doing to the specified FILE
 * --log-file-format=FMT    log updates using the specified FMT
 * */

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.processoutput4j.output.ConsoleOutputProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName : RsyncUtil
 * @Description : Rsync工具类
 * @Author : Mr.Wang
 * @Date : 2020-09-29 16:06
 **/
@SuppressWarnings("all")
public class RsyncUtil {
    private static final Logger logger    = LoggerFactory.getLogger(RsyncUtil.class);
    public static final String rsyncPwd  = "UltramanBeatLM123";
    private static RsyncUtil rsyncUtil;

    private static String buildRemoteUrl(String address, String user,String module, List<String> files) {
        StringBuilder sb = new StringBuilder();
        sb.append(user).append("@").append(address).append("::");
        if (files != null && files.size() > 0) {
            for (String file : files) {
                sb.append(module).append("/").append(file);
            }
        } else {
            sb.append(module);
        }
        return sb.toString();
    }

    /**
     * @Description : 判断本地目标是否存在
     * @Param       : [dest]
     * @return      : com.smarteast.review.common.response.BaseResponse
     * @Author      : Mr.Wang
     * @Date        : 2020-10-09 15:24
     **/
    public static boolean exist(String dest) {
        try {
            RSync rsync = new RSync()
                    .source("")
                    .destination(dest)
                    .archive(true)
                    .recursive(true)
                    .listOnly(true)
                    // 忽略隐藏文件夹
                    .exclude("\\.*/")
                    .timeout(30)
                    ;
            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
    }

    /**
     * @Description : 判断远程目标是否存在
     * @Param       : [address, user, password, isEncrypt, module, file]
     * @return      : boolean
     * @Author      : Mr.Wang
     * @Date        : 2020-10-09 16:49
     **/
    public static BaseResponse exist(String address, String user, String password,
                                boolean isEncrypt, String module, String remote) {
        logger.info(">>> remote      : {}", remote);
        String passwordFile = getPwdFileName();

        try {
            if (isEncrypt) {
                password = decrypt(password, rsyncPwd);
            }
            if (!passwordWrite(passwordFile, password)) {
                logger.error("create password file error!");
                return BaseResponse.fail("create password file error!");
            }
            String remoteUrl = buildRemoteUrl(address, user, module, strToList(remote));

            RSync rsync = new RSync()
                    .source(remoteUrl)
                    .destination("")
                    .archive(true)
                    .compress(true)
                    .verbose(true)
                    .recursive(true)
                    .passwordFile(passwordFile)
                    .listOnly(true)
                    // 忽略隐藏文件夹
                    .exclude("\\.*/")
                    .timeout(30)
                    ;

            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                logger.error(output.getStdErr());
                return BaseResponse.fail(output.getStdErr());
            }
            return BaseResponse.success(output.getStdOut());
        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        } finally {
//            passwordDelete(passwordFile);
        }
    }

    /**
     * @Description : 同步远端的文件到本地
     * @Param       : [address, user, password, isEncrypt, module, remote, local, isRemoveSource, isBackup, backupDir]
     * @return      : com.smarteast.review.common.response.BaseResponse
     * @Author      : Mr.Wang
     * @Date        : 2020-10-09 17:17
     **/
    public static BaseResponse syncFromRemote(String address, String user, String password, boolean isEncrypt,
                                              String module, String remote, String local,
                                              boolean isRemoveSource, boolean isBackup, String backupDir) {
        logger.info("begin download");
        logger.info(">>> remote      : {}", remote);
        logger.info(">>> local     : {}", local);

        String passwordFile = getPwdFileName();

        try {
            if (isEncrypt) {
                password = decrypt(password, rsyncPwd);
            }
            if (!passwordWrite(passwordFile, password)) {
                logger.error("create password file error!");
                return BaseResponse.fail("create password file error!");
            }
            String remoteUrl       = buildRemoteUrl(address, user, module, strToList(remote));
            String temp = local+ File.separator+ "temp";
            createTempPath(temp);

            RSync rsync = new RSync()
                    .source(remoteUrl)
                    .destination(local)
                    .archive(true)
                    .compress(true)
                    .verbose(true)
                    .recursive(true)
                    .relative(true)
                    .passwordFile(passwordFile)
                    .tempDir("temp")
                    ;

            if (isRemoveSource) {
                rsync.removeSourceFiles(true);
            }

            if (isBackup) {
                rsync.backup(true);
                if (!StringUtils.isEmpty(backupDir)) {
                    rsync.backupDir(backupDir);
                }
            }

            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                logger.error(">>> output : \n{}", output.getStdErr());
                return BaseResponse.fail(output.getStdErr());
            }
            logger.info("download sucess");
            return BaseResponse.success();
        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        } finally {
//            passwordDelete(passwordFile);
        }
    }

    /**
     * @Description : 同步本地文件到远端
     * @Param       : [address, user, password, isEncrypt, module, local, remote, isRemoveSource, isBackup, backupDir]
     * @return      : com.smarteast.review.common.response.BaseResponse
     * @Author      : Mr.Wang
     * @Date        : 2020-10-09 17:17
     **/
    public BaseResponse syncToRemote(String address, String user, String password, boolean isEncrypt,
                                            String module,  String local, String remote,
                                            boolean isRemoveSource, boolean isBackup, String backupDir) {
        logger.info(">>> local      : {}", local);
        logger.info(">>> remote     : {}", remote);

        String passwordFile = getPwdFileName();

        try {
            if (isEncrypt) {
                password = decrypt(password, rsyncPwd);
            }
            if (!passwordWrite(passwordFile, password)) {
                logger.error("create password file error!");
                return BaseResponse.fail("create password file error!");
            }
            String remoteUrl       = buildRemoteUrl(address, user, module, strToList(remote));
            RSync rsync = new RSync()
                    .source(local)
                    .destination(remoteUrl)
                    .archive(true)
                    .compress(true)
                    .verbose(true)
                    .recursive(true)
                    .passwordFile(passwordFile)
                    ;

            if (isRemoveSource) {
                rsync.removeSourceFiles(true);
            }

            if (isBackup) {
                rsync.backup(true);
                if (!StringUtils.isEmpty(backupDir)) {
                    rsync.backupDir(backupDir);
                }
            }

            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                logger.error(">>> output : \n{}", output.getStdErr());
                return BaseResponse.fail(output.getStdErr());
            }
            return BaseResponse.success();

        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        } finally {
            passwordDelete(passwordFile);
        }
    }


    /**
     * @Description : 同步远端的多个文件到本地
     * @Param       : [address, user, password, isEncrypt, module, remote, local, isRemoveSource, isBackup, backupDir]
     * @return      : com.smarteast.review.common.response.BaseResponse
     * @Author      : Mr.Wang
     * @Date        : 2020-10-09 17:17
     **/
    public BaseResponse syncMultiFromRemote(String address, String user, String password, boolean isEncrypt,
                                                   String module,  List<String> remote, String local,
                                                   boolean isRemoveSource, boolean isBackup, String backupDir) {
        logger.info(">>> remote      : {}", remote);
        logger.info(">>> local     : {}", local);

        String passwordFile = getPwdFileName();

        try {
            if (isEncrypt) {
                password = decrypt(password, rsyncPwd);
            }
            if (!passwordWrite(passwordFile, password)) {
                logger.error("create password file error!");
                return BaseResponse.fail("create password file error!");
            }
            String remoteUrl       = buildRemoteUrl(address, user, module, remote);
            String temp = local+ File.separator+ "temp";
            createTempPath(temp);
            RSync rsync = new RSync()
                    .source(remoteUrl)
                    .destination(local)
                    .archive(true)
                    .compress(true)
                    .verbose(true)
                    .recursive(true)
                    .passwordFile(passwordFile)
                    .tempDir("temp")
                    ;

            if (isRemoveSource) {
                rsync.removeSourceFiles(true);
            }

            if (isBackup) {
                rsync.backup(true);
                if (!StringUtils.isEmpty(backupDir)) {
                    rsync.backupDir(backupDir);
                }
            }

            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                logger.error(">>> output : \n{}", output.getStdErr());
                return BaseResponse.fail(output.getStdErr());
            }
            return BaseResponse.success();

        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        } finally {
            passwordDelete(passwordFile);
        }
    }

    /**
     * @Description : 同步本地多个文件到远端
     * @Param       : [address, user, password, isEncrypt, module, local, remote, isRemoveSource, isBackup, backupDir]
     * @return      : com.smarteast.review.common.response.BaseResponse
     * @Author      : Mr.Wang
     * @Date        : 2020-10-09 17:17
     **/
    public BaseResponse syncMultiToRemote(String address, String user, String password, boolean isEncrypt,
                                                 String module,  List<String> local, String remote,
                                                 boolean isRemoveSource, boolean isBackup, String backupDir) {
        logger.info(">>> local      : {}", local);
        logger.info(">>> remote     : {}", remote);

        String passwordFile = getPwdFileName();

        try {
            if (isEncrypt) {
                password = decrypt(password, rsyncPwd);
            }
            if (!passwordWrite(passwordFile, password)) {
                logger.error("create password file error!");
                return BaseResponse.fail("create password file error!");
            }
            String remoteUrl       = buildRemoteUrl(address, user, module, strToList(remote));
            RSync rsync = new RSync()
                    .sources(local)
                    .destination(remoteUrl)
                    .archive(true)
                    .compress(true)
                    .verbose(true)
                    .recursive(true)
                    .passwordFile(passwordFile)
                    ;

            if (isRemoveSource) {
                rsync.removeSourceFiles(true);
            }

            if (isBackup) {
                rsync.backup(true);
                if (!StringUtils.isEmpty(backupDir)) {
                    rsync.backupDir(backupDir);
                }
            }

            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                logger.error(">>> output : \n{}", output.getStdErr());
                return BaseResponse.fail(output.getStdErr());
            }
            return BaseResponse.success();
        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        } finally {
            passwordDelete(passwordFile);
        }
    }

    /**
     * @Description : 创建临时目录
     * @Param       : []
     * @return      : void
     * @Author      : Mr.Wang
     * @Date        : 2020-10-09 17:59
     **/
    private static void createTempPath(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /*
     * @Description : 本地同步
     * @Param       : [src, dest]
     * @return      : com.smarteast.review.common.response.BaseResponse
     * @Author      : Mr.Wang
     * @Date        : 2020-09-29 17:22
     **/
    public static BaseResponse sync(String src, String dest) {
        try {
            RSync rsync = new RSync()
                    .source(src)
                    .destination(dest)
                    .archive(true)
                    .compress(true)
                    .verbose(true)
                    .recursive(true)
                    ;
            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                BaseResponse.fail(output.getStdErr());
            }
        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        }

        return BaseResponse.success();
    }

    /*
     * @Description : 本地同步,并且可选的删除源文件
     * @Param       : [src, dest, isRemoveSource, isBackup, backupDir]
     * @return      : com.smarteast.review.common.response.BaseResponse
     * @Author      : Mr.Wang
     * @Date        : 2020-09-29 17:22
     **/
    public static BaseResponse sync(String src, String dest, boolean isRemoveSource, boolean isBackup, String backupDir) {
        try {
            RSync rsync = new RSync()
                    .source(src)
                    .destination(dest)
                    .archive(true)
                    .compress(true)
                    .verbose(true)
                    .recursive(true)
                    ;

            if (isRemoveSource) {
                rsync.removeSourceFiles(true);
            }
            if (isBackup) {
                rsync.backup(true);
                if (!StringUtils.isEmpty(backupDir)) {
                    rsync.backupDir(backupDir);
                }
            }

            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                logger.error(output.getStdErr());
                BaseResponse.fail(output.getStdErr());
            }
        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        }

        return BaseResponse.success();
    }

    /*
     * @Description : 本地多文件同步
     * @Param       : [src, dest, isRemoveSource, isBackup, backupDir]
     * @return      : com.smarteast.review.common.response.BaseResponse
     * @Author      : Mr.Wang
     * @Date        : 2020-09-29 17:24
     **/
    public static BaseResponse sync(List<String> src, String dest, boolean isRemoveSource, boolean isBackup, String backupDir) {
        try {
            RSync rsync = new RSync()
                    .sources(src)
                    .destination(dest)
                    .archive(true)
                    .compress(true)
                    .verbose(true)
                    .recursive(true)
                    ;

            if (isRemoveSource) {
                rsync.removeSourceFiles(true);
            }
            if (isBackup) {
                rsync.backup(true);
                if (!StringUtils.isEmpty(backupDir)) {
                    rsync.backupDir(backupDir);
                }
            }

            CollectingProcessOutput output = rsync.execute();
            if (output.getExitCode() > 0) {
                logger.error(output.getStdErr());
                BaseResponse.fail(output.getStdErr());
            }
        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        }
        return BaseResponse.success();
    }

    private static void passwordDelete(String fileName) {
        try {
            File file = new File(fileName);
            if(file.exists()){
                file.delete();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private static String getPwdFileName() {
        return "." + UUID.randomUUID().toString() + ".mamba.out";
//        return "rsync" + ".mamba.out";
    }

    private static boolean passwordWrite(String fileName, String content) {
        boolean result=true;
        int i=0;
        do {
            i++;
            try {
                File file = new File(fileName);
                if(!file.exists()){
                    file.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write(content);
                bw.close();
                result = ShellCmdExecuter.doExecuteCmd("chmod 600 " + fileName);
            } catch (IOException e) {
                logger.error("", e);
                result = false;
            }
            if (!result){
                passwordDelete(fileName);
            }
        } while (!result && i<3);
        return result;
        //        return true;

    }

    private static List<String> strToList(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        list.add(str);
        return list;
    }

    public static String encryt(String str, String password){
        BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword(password);
        return  basicTextEncryptor.encrypt(str);
    }

    public static String decrypt(String src, String password) {
        BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword(password);
        return basicTextEncryptor.decrypt(src);
    }

    public static BaseResponse existByShell(String address, String user, String password,
                                    boolean isEncrypt, String module,String remote){
        logger.info(">>> remote   : {}", remote);

        String passwordFile = getPwdFileName();
        try {
            if (isEncrypt) {
                password = decrypt(password, rsyncPwd);
            }
            if (!passwordWrite(passwordFile, password)) {
                logger.error("create password file error!");
                return BaseResponse.fail("create password file error!");
            }
            String remoteUrl       = buildRemoteUrl(address, user, module, strToList(remote));
            String cmd = new StringBuilder("rsync --password-file=").append(passwordFile).append(" ").append(remoteUrl).toString();
            if (ShellCmdExecuter.doExecuteCmd(cmd)){
                return BaseResponse.success();
            } else {
                logger.error("execute cmd fail " + cmd);
                return BaseResponse.fail("execute cmd fail " + cmd);
            }
        } catch (Exception e) {
            logger.error("", e);
            return BaseResponse.fail(e.getMessage());
        } finally {
            passwordDelete(passwordFile);
        }
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString());
    }

//    public static void main(String[] args) {
//        System.out.println(RsyncUtil.encryt("123",rsyncPwd));
//    }
}

