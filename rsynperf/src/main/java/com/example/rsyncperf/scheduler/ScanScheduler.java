package com.example.rsyncperf.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScanScheduler implements SchedulingConfigurer {
    Logger logger = LoggerFactory.getLogger(ScanScheduler.class);

    @Value("${rsync.scan.interval}")
    private long scanInterval;
    @Autowired
    private ScanService scanService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedRateTask(new ScanThread(scanService),scanInterval);

    }
}
