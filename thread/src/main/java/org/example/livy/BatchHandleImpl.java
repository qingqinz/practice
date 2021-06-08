package org.example.livy;

import org.example.livy.entity.BatchBody;
import org.example.livy.entity.BatchInfo;
import org.example.livy.entity.LogInfo;
import org.apache.livy.shaded.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenxh on 2018/7/26.
 */
public class BatchHandleImpl extends AbstractHandle {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private int batchId;

    public int getBatchId() {
        return batchId;
    }

    public BatchHandleImpl(HttpConf config, LivyConnection conn, ScheduledExecutorService executor) {
        super(config, conn, executor);
    }

    public Integer startBatch(BatchBody body) {
        try {
            BatchInfo status = conn.post(body, BatchInfo.class, "batches");

            if (isCancelPending) {
                stopBatch(status.getId());
            }
            batchId = status.getId();

            pollTask = executor.schedule(new BatchPollTask(initialPollInterval),
                    initialPollInterval, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            setResult(null, e);
        }
        return batchId;
    }

    private void stopBatch(Integer id) {

    }

    public LogInfo fetchLogs(int from, int size) {
        if (batchId <= 0) {
            return null;
        }

        try {
            return conn.get(LogInfo.class, "batches/%d/log?from=%d&size=%d", batchId, from, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class BatchPollTask implements Runnable {

        private long currentInterval;

        BatchPollTask(long currentInterval) {
            this.currentInterval = currentInterval;
        }

        @Override
        public void run() {
            try {
                BatchInfo status = null;
                try {
                    status = conn.get(BatchInfo.class, "batches/%d/state", batchId);
                } catch (ConnectTimeoutException e) {
                    // 超时异常，暂时忽略，因为下一次会再次请求
                    logger.error("get session timeout:", e);
                }

                boolean finished = false;
                //取到了状态
                if (status != null) {
                    Throwable error = null;
                    switch (status.getState()) {
                        case "success":
                            finished = true;
                            break;
                        case "running":
                            break;
                        case "starting":
                            break;
                        case "dead":
                            finished = true;
                            break;
                        case "killed":
                            finished = true;
                            break;

                        default:
                            // Nothing to do.
                    }
                    if (finished) {
                        setResult(status.getState(), error);
                    }
                }

                if (!finished) {
                    currentInterval = Math.min(currentInterval * 2, maxPollInterval);
                    pollTask = executor.schedule(this, currentInterval, TimeUnit.MILLISECONDS);
                }
            } catch (Exception e) {
                setResult(null, e);
            }
        }
    }

    public LogInfo getAllLogInfo() {
        try {
            return conn.get(LogInfo.class, "from=0&size=" + Integer.MAX_VALUE, "batches/%d/log", batchId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
