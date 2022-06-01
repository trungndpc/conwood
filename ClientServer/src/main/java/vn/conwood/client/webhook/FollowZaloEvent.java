package vn.conwood.client.webhook;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.common.status.StatusUser;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.client.controller.AuthenController;
import vn.conwood.client.service.UserService;
import vn.conwood.client.webhook.zalo.FollowZaloWebhookMessage;
import vn.conwood.client.webhook.zalo.ZaloWebhookMessage;

@Component
public class FollowZaloEvent extends ZaloEvent{

    private static final Logger LOGGER = LogManager.getLogger(FollowZaloEvent.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean process(ZaloWebhookMessage zaloWebhookMessage) {
        try{
            FollowZaloWebhookMessage followZaloMsg = (FollowZaloWebhookMessage) zaloWebhookMessage;
            UserEntity userEntity = userService.findByFollowerId(followZaloMsg.follower.id);
            if (userEntity == null) {
                userEntity = new UserEntity();
                userEntity.setStatus(StatusUser.WAIT_COMPLETE_PROFILE);
            }
            userEntity.setZaloId(String.valueOf(followZaloMsg.userIdByApp));
            userEntity.setFollowerId(followZaloMsg.follower.id);
            userEntity = userService.saveOrUpdate(userEntity);
            //flow register async
            AuthenController.MAP_FOLLOWER.put(String.valueOf(followZaloMsg.userIdByApp), userEntity.getId());
            return true;
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
