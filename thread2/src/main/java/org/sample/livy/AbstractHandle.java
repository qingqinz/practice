package org.sample.livy;

import java.util.concurrent.*;

/**
 * Created by chenxh on 2018/7/25.
 */
public abstract class AbstractHandle implements Future<String> {

    protected final LivyConnection conn;
    protected final ScheduledExecutorService executor;
    protected final Object lock;

    protected final long initialPollInterval;
    protected final long maxPollInterval;

    protected String result;
    protected Throwable error;
    protected volatile boolean isDone;
    protected volatile boolean isCancelled;
    protected volatile boolean isCancelPending;
    protected volatile ScheduledFuture<?> pollTask;

    public AbstractHandle(HttpConf config,
                          LivyConnection conn,
                          ScheduledExecutorService executor) {
        this.conn = conn;
        this.executor = executor;
        this.lock = new Object();
        this.isDone = false;

        this.initialPollInterval = config.getTimeAsMs(HttpConf.Entry.JOB_INITIAL_POLL_INTERVAL);
        this.maxPollInterval = config.getTimeAsMs(HttpConf.Entry.JOB_MAX_POLL_INTERVAL);

        if (initialPollInterval <= 0) {
            throw new IllegalArgumentException("Invalid initial poll interval.");
        }
        if (maxPollInterval <= 0 || maxPollInterval < initialPollInterval) {
            throw new IllegalArgumentException(
                    "Invalid max poll interval, or lower than initial interval.");
        }

        this.isCancelPending = false;

    }


    @Override
    public String get() throws ExecutionException, InterruptedException {
        try {
            return get(true, -1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            // Not gonna happen.
            throw new RuntimeException(te);
        }
    }

    @Override
    public String get(long timeout, TimeUnit unit)
            throws ExecutionException, InterruptedException, TimeoutException {
        return get(false, timeout, unit);
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public boolean cancel(final boolean mayInterrupt) {
        // Do a best-effort to detect if already cancelled, but the final say is always
        // on the server side. Don't block the caller, though.
        if (!isCancelled && !isCancelPending) {
            isCancelPending = true;
            return true;
        }

        return false;
    }

    protected String get(boolean waitIndefinitely, long timeout, TimeUnit unit)
            throws ExecutionException, InterruptedException, TimeoutException {
        if (!isDone) {
            synchronized (lock) {
                if (waitIndefinitely) {
                    while (!isDone) {
                        lock.wait();
                    }
                } else {
                    long now = System.nanoTime();
                    long deadline = now + unit.toNanos(timeout);
                    while (!isDone && deadline > now) {
                        lock.wait(TimeUnit.NANOSECONDS.toMillis(deadline - now));
                        now = System.nanoTime();
                    }
                    if (!isDone) {
                        throw new TimeoutException();
                    }
                }
            }
        }
        if (isCancelled) {
            throw new CancellationException();
        }
        if (error != null) {
            throw new ExecutionException(error);
        }
        return result;
    }

    protected void setResult(String result, Throwable error) {
        if (!isDone) {
            synchronized (lock) {
                if (!isDone) {
                    this.result = result;
                    this.error = error;
                    this.isDone = true;
                }
                lock.notifyAll();
            }
        }
    }
}
