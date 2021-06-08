/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.livy;

import org.example.livy.entity.LogInfo;
import org.example.livy.entity.SessionBody;
import org.example.livy.entity.SessionState;
import org.example.livy.entity.StatementBody;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.apache.livy.client.common.HttpMessages.SessionInfo;

public class StatementClient extends AbstractClient {

    private final HttpConf config;
    private final LivyConnection conn;
    private final int sessionId;
    private final ScheduledExecutorService executor;

    private boolean stopped;

    public StatementClient(URI uri, HttpConf httpConf) {
        this.config = httpConf;
        this.stopped = false;

        try {
            Map<String, String> sessionConf = new HashMap<>();
            for (Map.Entry<String, String> e : config) {
                sessionConf.put(e.getKey(), e.getValue());
            }
            SessionBody body = new SessionBody();
            body.setConf(sessionConf);
            this.conn = new LivyConnection(uri, httpConf);
            this.sessionId = conn.post(body, SessionInfo.class, "sessions").id;
        } catch (Exception e) {
            throw propagate(e);
        }

        // Because we only have one connection to the server, we don't need more than a single
        // threaded executor here.
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "HttpClient-" + sessionId);
            t.setDaemon(true);
            return t;
        });

    }


    @Override
    public synchronized void stop(boolean shutdownContext) {
        if (!stopped) {
            executor.shutdownNow();
            try {
                if (shutdownContext) {
                    conn.delete(Map.class, "sessions/%s", sessionId);
                }
            } catch (Exception e) {
                throw propagate(e);
            } finally {
                try {
                    conn.close();
                } catch (Exception e) {
                    // Ignore.
                }
            }
            stopped = true;
        }
    }

    @Override
    public LogInfo fetchLogs(int from, int size) {
        try {
            return conn.get(LogInfo.class, String.format("from=%d&size=%d", from, size), "sessions/%d/log", sessionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected RuntimeException propagate(Exception cause) {
        if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else {
            throw new RuntimeException(cause);
        }
    }

    @Override
    public boolean isStoped() {
        return stopped;
    }

    // For testing.
    public int getSessionId() {
        return sessionId;
    }

    public StatementHandleImpl sendStatement(StatementBody body) {
        StatementHandleImpl handle = new StatementHandleImpl(config, conn, sessionId, executor);
        handle.startStatement(body);
        return handle;
    }

    public boolean waitSessionOk() {
        SessionHandleImpl handle = new SessionHandleImpl(config, conn, sessionId, executor);
        return handle.waitSessionOk();
    }

    public boolean validateSession() {
        try {
            SessionState status = conn.get(SessionState.class, "sessions/%d", sessionId);
            return status.getState().ordinal() <= 3;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Object> getSessionAppInfo() {
        try {
            SessionState status = conn.get(SessionState.class, "sessions/%d", sessionId);
            return status.getAppInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "StatementClient{" +
                "sessionId=" + sessionId +
                ", stopped=" + stopped +
                '}';
    }
}
