package vn.conwood.client.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import vn.conwood.client.config.AppConfig;
import vn.conwood.client.wrapper.entity.ZaloMessage;
import vn.conwood.client.wrapper.entity.ZaloUserEntity;
import vn.conwood.util.BeanUtil;

import java.nio.charset.StandardCharsets;


public class ZaloService {
    public static final ZaloService INSTANCE = new ZaloService();
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AppConfig appConfig;
    private static final String GET_ACCESS_TOKEN_URL = "https://oauth.zaloapp.com/v3/access_token?app_id={1}&app_secret={2}&code={3}";
    private static final String GET_USER_INFO = "https://graph.zalo.me/v2.0/me?fields=id,name,picture,birthday,gender&access_token=";
    private static final String END_POINT = "https://openapi.zalo.me/v2.0/oa/message?access_token={1}";

    public ZaloService() {
        this.appConfig = BeanUtil.getBean(AppConfig.class);
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public String getAccessToken(String oauthCode) throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(GET_ACCESS_TOKEN_URL, String.class,
                this.appConfig.ZALO_APP_ID,
                this.appConfig.ZALO_SECRET_APP,
                oauthCode);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new Exception(responseEntity.getBody());
        }
        JSONObject json = new JSONObject(responseEntity.getBody());
        return  json.getString("access_token");
    }

    public ZaloUserEntity getUserInfo(String accessToken) throws Exception {
        String url = new StringBuffer(GET_USER_INFO).append(accessToken).toString();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new Exception(responseEntity.getBody());
        }
        JSONObject json = new JSONObject(responseEntity.getBody());
        ZaloUserEntity entity = new ZaloUserEntity();
        entity.setId(json.getString("id"));
        entity.setName(json.getString("name"));
        entity.setGender(json.optString("gender"));
        entity.setBirthday(json.getString("birthday"));
        entity.setAvatar(json.getJSONObject("picture").getJSONObject("data").getString("url"));
        return entity;
    }

    public boolean send(String followerId, ZaloMessage message) throws JsonProcessingException {
        JSONObject msWrapper = new JSONObject();
        JSONObject recipient =  new JSONObject();
        recipient.put("user_id", followerId);
        msWrapper.put("recipient", recipient);
        String value = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
        msWrapper.put("message", new JSONObject(value));
        ResponseEntity<String> zaloResponseResponseEntity = restTemplate.postForEntity(END_POINT,
                msWrapper.toString(), String.class,
                this.appConfig.ZALO_OA_ACCESS_TOKEN);
        return zaloResponseResponseEntity.getStatusCode() == HttpStatus.OK;
    }
}
