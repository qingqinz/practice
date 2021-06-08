package threadpool;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        System.out.println(new Date());
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Task(),10,5, TimeUnit.SECONDS);
//        scheduledThreadPoolExecutor.schedule(new Task(),10,TimeUnit.SECONDS);
//        scheduledThreadPoolExecutor.shutdown();
//        Executors.newScheduledThreadPool()

    }
//    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();

}

class Task implements Runnable {

    @Override
    public void run() {

        System.out.println("before"+new Date());
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("after"+new Date());

    }
}
