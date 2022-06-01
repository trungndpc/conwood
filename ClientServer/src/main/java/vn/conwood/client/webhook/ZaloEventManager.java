package vn.conwood.client.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.client.service.UserService;
import vn.conwood.client.webhook.zalo.FollowZaloWebhookMessage;
import vn.conwood.client.webhook.zalo.UserReceivedZNSMessage;
import vn.conwood.client.webhook.zalo.UserSendMessage;

@Service
public class ZaloEventManager {
    private static final Logger LOGGER = LogManager.getLogger(ZaloEventManager.class);

    @Autowired
    private WebhookSessionManager webhookSessionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FollowZaloEvent followZaloEvent;

    @Autowired
    private UserReceivedZNSEvent userReceivedZNSEvent;

    @Autowired
    private UserService userService;


    public void doing(JSONObject zaloMsg) throws Exception {
        String eventName = zaloMsg.optString("event_name", "");
        switch (eventName) {
            case "follow":
                FollowZaloWebhookMessage userFollow = objectMapper.readValue(zaloMsg.toString(), FollowZaloWebhookMessage.class);
                followZaloEvent.process(userFollow);
                return;
            case "user_received_message":
                UserReceivedZNSMessage receivedZNSMessage = objectMapper.readValue(zaloMsg.toString(), UserReceivedZNSMessage.class);
                userReceivedZNSEvent.process(receivedZNSMessage);
                return;
            case "user_send_text":
                long userByAppId = zaloMsg.getLong("user_id_by_app");
                UserEntity userEntity = userService.findByZaloId(userByAppId);
                if (userEntity == null) {
                    throw new Exception("not found user user_id_by_app: " + userByAppId + ", zaloMsg: " + zaloMsg);
                }
                UserSendMessage userSendText = objectMapper.readValue(zaloMsg.toString(), UserSendMessage.class);
                String text = userSendText.message.text;
                Object session = webhookSessionManager.getCurrentSession(userEntity.getId());

                if (session != null) {
                    LOGGER.error("session: " + session.getClass());
                }
                break;
        }
    }

}
