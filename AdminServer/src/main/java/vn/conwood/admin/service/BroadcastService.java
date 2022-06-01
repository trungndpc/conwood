package vn.conwood.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.conwood.admin.woker.task.PostBroadcastTask;
import vn.conwood.admin.woker.Scheduler;
import vn.conwood.admin.woker.task.ZNSBroadcastTask;
import vn.conwood.common.status.StatusBroadcast;
import vn.conwood.common.type.TypeBroadcast;
import vn.conwood.jpa.entity.BroadcastEntity;
import vn.conwood.jpa.repository.BroadcastRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;


@Service
public class BroadcastService {

    @Autowired
    private BroadcastRepository broadcastRepository;

    @Autowired
    private Scheduler scheduler;

    public BroadcastEntity create(BroadcastEntity entity) {
        entity.setId(0);
        entity.setStatus(StatusBroadcast.INIT);
        return broadcastRepository.saveAndFlush(entity);
    }

    public BroadcastEntity update(BroadcastEntity entity) {
        return broadcastRepository.saveAndFlush(entity);
    }

    public Page<BroadcastEntity> find(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return broadcastRepository.findAll(pageable);
    }

    public BroadcastEntity get(int id) {
        return broadcastRepository.getOne(id);
    }

    public int updateStatus(int id, int status) {
        BroadcastEntity broadcastEntity = broadcastRepository.getOne(id);
        if (broadcastEntity.getStatus() == StatusBroadcast.APPROVED && status == StatusBroadcast.CANCELED) {
            String jobId = broadcastEntity.getJobId();
            scheduler.remove(jobId);
            broadcastEntity.setStatus(status);
            broadcastRepository.saveAndFlush(broadcastEntity);
            return 0;
        }

        if (broadcastEntity.getStatus() == StatusBroadcast.INIT && status == StatusBroadcast.APPROVED) {
            LocalDateTime triggerTime =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(broadcastEntity.getTimeStart()),
                            TimeZone.getDefault().toZoneId());

            if (broadcastEntity.getType() == TypeBroadcast.BROADCAST_NORMAL_POST) {

                PostBroadcastTask postNormalBroadcastTask = new PostBroadcastTask();
                postNormalBroadcastTask.setBroadcastId(broadcastEntity.getId());
                postNormalBroadcastTask.setPostId(broadcastEntity.getPostId());
                postNormalBroadcastTask.setUids(broadcastEntity.getUserIds());
                String jobId = scheduler.addJob(triggerTime, postNormalBroadcastTask);
                broadcastEntity.setJobId(jobId);
                broadcastEntity.setStatus(status);
                broadcastRepository.saveAndFlush(broadcastEntity);
            }

            if (broadcastEntity.getType() == TypeBroadcast.REQUEST_REGISTER_ZNS) {
                ZNSBroadcastTask znsBroadcastTask = new ZNSBroadcastTask();
                znsBroadcastTask.setBroadcastId(broadcastEntity.getId());
                znsBroadcastTask.setUids(broadcastEntity.getUserIds());
                String jobId = scheduler.addZNSBroadcastJob(triggerTime, znsBroadcastTask);
                broadcastEntity.setJobId(jobId);
                broadcastEntity.setStatus(status);
                broadcastRepository.saveAndFlush(broadcastEntity);
            }
            return 0;
        }

        broadcastEntity.setStatus(status);
        broadcastRepository.saveAndFlush(broadcastEntity);
        return 0;
    }




}
