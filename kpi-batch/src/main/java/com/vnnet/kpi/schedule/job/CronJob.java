package com.vnnet.kpi.schedule.job;

import com.vnnet.kpi.schedule.service.BatchService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;


public class CronJob implements Job {

    @Autowired
    private BatchService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            service.process();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
