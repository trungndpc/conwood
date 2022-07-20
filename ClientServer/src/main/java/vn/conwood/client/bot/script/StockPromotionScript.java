package vn.conwood.client.bot.script;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.conwood.client.bot.StockPromotionSession;
import vn.conwood.client.bot.User;
import vn.conwood.client.webhook.WebhookSessionManager;
import vn.conwood.util.BeanUtil;


public class StockPromotionScript {
    private static final Logger LOGGER = LogManager.getLogger();
    private final static WebhookSessionManager WEBHOOK_SESSION_MANAGER = BeanUtil.getBean(WebhookSessionManager.class);

    private User user;
    private StockPromotionSession session;


    private ObjectMapper objectMapper = new ObjectMapper();

    public StockPromotionScript(User user) throws JsonProcessingException {
        this.user = user;
    }



}
