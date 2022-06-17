package vn.conwood.client.webhook;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.common.status.StatusUser;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.client.service.UserService;
import vn.conwood.client.webhook.zalo.UserReceivedZNSMessage;
import vn.conwood.client.webhook.zalo.ZaloWebhookMessage;

@Component
public class UserReceivedZNSEvent extends ZaloEvent{
    private static final Logger LOGGER = LogManager.getLogger(UserReceivedZNSEvent.class);

    @Autowired
    private UserService userService;


    @Override
    public boolean process(ZaloWebhookMessage zaloWebhookMessage) {
        try{
            UserReceivedZNSMessage userReceivedZNSMessage = ((UserReceivedZNSMessage)zaloWebhookMessage);
            UserReceivedZNSMessage.Content message = userReceivedZNSMessage.getMessage();
            String phoneTracking = message.getTracking_id();
            if (phoneTracking != null && !StringUtils.isEmpty(phoneTracking)) {
                UserEntity userEntity = userService.findByPhone(phoneTracking);
                if (userEntity != null) {
                    if (userEntity.getStatus() != StatusUser.WAITING_ACTIVE) {
                        throw new Exception("status user is not waiting active");
                    }
                    userEntity.setFollowerId(userReceivedZNSMessage.recipient.id);
                    userEntity.setZaloId(String.valueOf(zaloWebhookMessage.userIdByApp));
                    userService.saveOrUpdate(userEntity);
                }
            }
            return true;
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
