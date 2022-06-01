package vn.conwood.admin.woker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.admin.woker.task.UpdateStatusMatchTask;


@Service
public class UpdateStatusMatchFootballWorker {
    private static final Logger LOGGER = LogManager.getLogger(UpdateStatusMatchFootballWorker.class);


}
