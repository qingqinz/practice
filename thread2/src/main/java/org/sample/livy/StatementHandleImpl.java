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

package org.sample.livy;

import org.sample.livy.entity.StatementBody;
import org.sample.livy.entity.StatementStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatementHandleImpl extends AbstractHandle {

    private final long sessionId;
    private long statementId;
    private volatile Map<String, Object> data;
    private volatile String evalue;
    private volatile List<String> traceback;


    StatementHandleImpl(
            HttpConf config,
            LivyConnection conn,
            long sessionId,
            ScheduledExecutorService executor) {
        super(config, conn, executor);
        this.sessionId = sessionId;
    }


    @Override
    public boolean cancel(final boolean mayInterrupt) {
        if (!isCancelled && !isCancelPending) {
            isCancelPending = true;
            if (statementId > -1) {
                sendCancelRequest(statementId);
            }
            return true;
        }

        return false;
    }


    private void sendCancelRequest(final long id) {
        executor.submit(() -> {
            try {
                conn.post(null, Void.class, "sessions/%d/statements/%d/cancel", sessionId, id);
            } catch (Exception e) {
                setResult(null, e);
            }
        });
    }


    public void startStatement(StatementBody body) {
        Runnable task = () -> {
            try {
                StatementStatus status = conn.post(body, StatementStatus.class, "sessions/%d/statements", sessionId);

                if (isCancelPending) {
                    sendCancelRequest(status.getId());
                }

                statementId = status.getId();

                pollTask = executor.schedule(new StatementPollTask(initialPollInterval),
                        initialPollInterval, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                setResult(null, e);
            }
        };
        executor.submit(task);
    }

    private class StatementPollTask implements Runnable {

        private long currentInterval;

        StatementPollTask(long currentInterval) {
            this.currentInterval = currentInterval;
        }

        @Override
        public void run() {
            try {

                StatementStatus status = conn.get(StatementStatus.class, "sessions/%d/statements/%d", sessionId, statementId);
                Throwable error = null;
                boolean finished = false;
                String result = "ok";

                switch (status.getState()) {
                    case available:
                        finished = true;
                        result = status.getOutput().getStatus();
                        if (!"ok".toLowerCase().equals(result)) {
                            evalue = status.getOutput().getEvalue();
                            traceback = status.getOutput().getTraceback();
                        }
                        data = status.getOutput().getData();
                        break;

                    case error:
                        // TODO: better exception.
                        error = new RuntimeException();
                        finished = true;
                        result = "error";
                        break;

                    case cancelled:
                        isCancelled = true;
                        finished = true;
                        break;

                    default:
                        // Nothing to do.
                }
                if (finished) {
                    setResult(result, error);
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

    public Map<String, Object> getData() {
        return data;
    }

    public String getEvalue() {
        return evalue;
    }

    public List<String> getTraceback() {
        return traceback;
    }
}
