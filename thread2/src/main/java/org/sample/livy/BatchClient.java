package org.sample.livy;

import org.sample.livy.entity.BatchBody;
import org.sample.livy.entity.LogInfo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by chenxh on 2018/7/24.
 */
public class BatchClient extends AbstractClient {

    private final HttpConf config;
    private final LivyConnection conn;
    private final ScheduledExecutorService executor;

    private volatile BatchHandleImpl batchHandle;

    private volatile boolean stoped;

    public BatchClient(URI uri, HttpConf config) {
        this.config = config;

        try {
            Map<String, String> sessionConf = new HashMap<>();
            for (Map.Entry<String, String> e : config) {
                sessionConf.put(e.getKey(), e.getValue());
            }
            this.conn = new LivyConnection(uri, config);
        } catch (Exception e) {
            throw propagate(e);
        }

        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "BatchClient");
            t.setDaemon(true);
            return t;
        });
    }


    public BatchHandleImpl startBatch(BatchBody body) {
        BatchHandleImpl handle = new BatchHandleImpl(config, conn, executor);
        handle.startBatch(body);
        batchHandle = handle;
        return handle;
    }


    @Override
    public void stop(boolean shutdownContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LogInfo fetchLogs(int from, int size) {
        if (batchHandle == null) {
            return null;
        }
        return batchHandle.fetchLogs(from, size);
    }

    @Override
    public boolean isStoped() {
        return stoped;
    }

    public LogInfo fetchAllLog() {
        if (batchHandle == null) {
            return null;
        }
        return batchHandle.getAllLogInfo();
    }
}
