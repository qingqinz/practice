package org.sample.livy;


import org.sample.livy.entity.LogInfo;

/**
 * Created by chenxh on 2018/7/26.
 */
public abstract class AbstractClient {

    public abstract void stop(boolean shutdownContext);

    public abstract LogInfo fetchLogs(int from, int size);

    protected RuntimeException propagate(Exception cause) {
        if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else {
            throw new RuntimeException(cause);
        }
    }

    public abstract boolean isStoped();

}
