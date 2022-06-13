package vn.conwood.admin.message.gift;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.conwood.admin.config.AppConfig;
import vn.conwood.admin.message.Message;
import vn.conwood.admin.message.User;
import vn.conwood.admin.wrapper.ZaloService;
import vn.conwood.admin.wrapper.entity.ZaloMessage;
import vn.conwood.util.BeanUtil;

import java.util.Arrays;

public class PhoneCardMessage extends Message {
    private static final Logger LOGGER = LogManager.getLogger(PhoneCardMessage.class);
    private static final AppConfig APP_CONFIG = BeanUtil.getBean(AppConfig.class);
    private static final String TITLE = "Chúc mừng !!!";
    private static final String SUB_TITLE_FORMAT = "Chúc mừng quý cửa hàng %s đã nhận được thẻ điện thoại %s từ chương trình khuyến " +
            "mãi của câu lạc bộ INSEE Cửa Hàng Ngoại Hạng. Vui lòng bấm vào tin nhắn để nhận được phần thưởng.";
    private String storeName;
    private String giftTitle;
    private int giftId;

    public PhoneCardMessage(User user, String storeName, String giftTitle, int giftId) {
        super(user);
        this.storeName = storeName;
        this.giftTitle = giftTitle;
        this.giftId = giftId;
    }

    public ZaloMessage build() {
        String subTitle = String.format(SUB_TITLE_FORMAT, storeName, giftTitle);
        ZaloMessage.Attachment.Payload.Element.Action action = new ZaloMessage.Attachment.Payload.Element.Action();
        action.type = "oa.open.url";
        action.url = APP_CONFIG.CLIENT_DOMAIN + "/qua-tang/" + giftId;

        ZaloMessage.Attachment.Payload.Element element = new ZaloMessage.Attachment.Payload.Element();
        element.title = TITLE;
        element.subtitle = subTitle;
        element.imageUrl =  "https://media.istockphoto.com/vectors/gift-card-with-red-bow-vector-id865405838?k=20&m=865405838&s=612x612&w=0&h=UYCSnYgcJH0ln-n8D6FlubgnMnpOq2PzaQOG8FOou8o=";
        element.action = action;
        return ZaloMessage.toListTemplate(Arrays.asList(element));
    }

    @Override
    public boolean send() {
        try{
            ZaloMessage message = build();
            ZaloService.INSTANCE.send(getUser().getFollowerId(), message);
            return true;
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
