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

import org.apache.livy.shaded.apache.http.conn.ConnectTimeoutException;
import org.example.livy.entity.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionHandleImpl extends AbstractHandle {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final long sessionId;

    public SessionHandleImpl(
            HttpConf config,
            LivyConnection conn,
            long sessionId,
            ScheduledExecutorService executor) {
        super(config, conn, executor);
        this.sessionId = sessionId;
    }

    public boolean waitSessionOk() {
        try {
            pollTask = executor.schedule(new SessionPollTask(initialPollInterval),
                    initialPollInterval, TimeUnit.MILLISECONDS);
            String result = get();
            return result.equals("idle") || result.equals("busy");
        } catch (Exception e) {
            logger.error("waitSession ready 异常", e);
            throw new RuntimeException(e);
        }
    }

    private class SessionPollTask implements Runnable {

        private long currentInterval;

        SessionPollTask(long currentInterval) {
            this.currentInterval = currentInterval;
        }

        @Override
        public void run() {
            try {
                SessionState status = null;
                try {
                    status = conn.get(SessionState.class, "sessions/%d", sessionId);
                } catch (ConnectTimeoutException e) {
                    logger.error("get session timeout:", e);
                }
                Throwable error = null;
                boolean finished = false;

                if (status != null) {
                    switch (status.getState()) {
                        case idle:
                        case busy:
                            finished = true;
                            break;

                        case not_started:
                        case starting:
                            break;
                        case shutting_down:
                        case error:
                        case dead:
                        case success:
                            error = new RuntimeException("session state is [" + status.getState() + "]");
                            finished = true;
                            break;

                        default:
                            // Nothing to do.
                    }
                    if (finished) {
                        setResult(status.getState().name(), error);
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


}
