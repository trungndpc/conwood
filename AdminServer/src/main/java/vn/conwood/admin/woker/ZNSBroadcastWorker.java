package vn.conwood.admin.woker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.jobs.annotations.Job;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.admin.service.BroadcastService;
import vn.conwood.admin.service.UserService;
import vn.conwood.admin.woker.task.ZNSBroadcastTask;
import vn.conwood.admin.wrapper.ZaloService;
import vn.conwood.common.status.StatusBroadcast;
import vn.conwood.common.status.StatusUser;
import vn.conwood.jpa.entity.BroadcastEntity;
import vn.conwood.jpa.entity.UserEntity;

import java.util.List;

@Component
public class ZNSBroadcastWorker {
    private static final String TEMPLATE_ID = "227387";
    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private UserService userService;

    @Autowired
    private BroadcastService broadcastService;

    @Job(name = "ZNS_BROADCAST", retries = 1)
    public void execute(ZNSBroadcastTask task) {
        int totalUid = 0;
        int totalUidAfterBuildUser = 0;
        int totalSuccessSend = 0;
        int error = 0;
        BroadcastEntity broadcastEntity = broadcastService.get(task.getBroadcastId());
        try{
            totalUid = task.getUids().size();
            List<Integer> uids = task.getUids();
            for (Integer uid: uids) {
                UserEntity userEntity = userService.findById(uid);
                if (userEntity.getStatus() == StatusUser.WAITING_ACTIVE) {
                    totalUidAfterBuildUser++;
                    JSONObject data = new JSONObject();
                    data.put("product_name", "Xi măng gỗ CONWOOD");
                    data.put("customer_code", userEntity.getInseeId());
                    data.put("customer_name", userEntity.getName());
                    data.put("order_date", "01/06/2022");
                    data.put("order_code", "ZNS" + userEntity.getId());
                    String phone = userEntity.getPhone();
                    if (phone.startsWith("0")) {
                        phone = phone.replaceFirst("0", "84");
                    }
                    ZaloService.INSTANCE.sendZNS(phone, TEMPLATE_ID, userEntity.getPhone(), data);
                    totalSuccessSend++;
                }
            }
        }catch (Exception e) {
            error = -1;
            LOGGER.error(e.getMessage(), e);
        }finally {
            broadcastEntity.setTotalUids(totalUid);
            broadcastEntity.setTotalUidsAfterBuildUser(totalUidAfterBuildUser);
            broadcastEntity.setTotalUidsSuccessSend(totalSuccessSend);
            if (error == 0) {
                broadcastEntity.setStatus(StatusBroadcast.DONE);
            }else {
                broadcastEntity.setStatus(StatusBroadcast.FAILED);
            }
            broadcastService.update(broadcastEntity);
        }
    }

}
