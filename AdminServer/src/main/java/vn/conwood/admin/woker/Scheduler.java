package vn.conwood.admin.woker;

import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.admin.woker.task.*;

import java.time.LocalDateTime;

@Component
public class Scheduler {

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    private PostBroadcastWorker service;

    @Autowired
    private ZNSBroadcastWorker znsBroadcastWorker;

    @Autowired
    private UpdateStatusMatchFootballWorker updateStatusMatchFootballWorker;

    public String addJob(LocalDateTime localDateTime, PostBroadcastTask job) {
        JobId jobId = jobScheduler.schedule(localDateTime, () -> service.execute(job));
        return jobId.asUUID().toString();
    }

    public String addZNSBroadcastJob(LocalDateTime localDateTime, ZNSBroadcastTask task) {
        JobId jobId = jobScheduler.schedule(localDateTime, () -> znsBroadcastWorker.execute(task));
        return jobId.asUUID().toString();
    }

    public void remove(String id) {
        jobScheduler.delete(JobId.parse(id));
    }

}
