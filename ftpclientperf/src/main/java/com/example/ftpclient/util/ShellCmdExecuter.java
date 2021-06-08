package com.example.ftpclient.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ShellCmdExecuter {
    private static final Logger logger = LoggerFactory.getLogger(ShellCmdExecuter.class);

    public static boolean doExecuteCmd(String cmd) {
        String msg;
        if(StringUtils.isEmpty(cmd)) {
            msg = "参数不能为空";
            logger.error(msg);
            return false;
        }

        ProcessBuilder pb = new ProcessBuilder();
        pb.command("/bin/sh", "-c", cmd);

        try {
            Process p = pb.start();

            Thread stdError = new Thread(new InputStreamRunnable(p.getErrorStream(), true));
            stdError.start();
            Thread stdInput = new Thread(new InputStreamRunnable(p.getInputStream(),true));
            stdInput.start();

            int code = p.waitFor();
            logger.info("shell cmd result code is {}", code);
            p.destroy();
            if (code != 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("shell cmd failed", e);
            return false;
        }
    }
}
