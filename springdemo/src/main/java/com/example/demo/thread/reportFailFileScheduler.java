package com.example.demo.thread;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

@Configuration
@EnableScheduling

public class reportFailFileScheduler implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
//        scheduledTaskRegistrar.addFixedDelayTask(new Task(),10);
//        scheduledTaskRegistrar.addFixedDelayTask(new IntervalTask(new Task(),10));
//        scheduledTaskRegistrar.addCronTask(new Task(),"*/5 * * * * *");
        scheduledTaskRegistrar.addTriggerTask(new Task(), new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String cron = "*/5 * * * * *";
                return  new CronTrigger(cron).nextExecutionTime(triggerContext);
            }
        });

    }


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


