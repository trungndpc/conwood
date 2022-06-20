package vn.conwood.admin.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import vn.conwood.admin.config.AppConfig;
import vn.conwood.admin.wrapper.entity.ZaloMessage;
import vn.conwood.admin.wrapper.entity.ZaloUserEntity;
import vn.conwood.util.BeanUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class ZaloService {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ZaloService INSTANCE = new ZaloService();
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AppConfig APP_CONFIG;
    private static final String GET_ACCESS_TOKEN_URL = "https://oauth.zaloapp.com/v3/access_token?app_id={1}&app_secret={2}&code={3}";
    private static final String GET_USER_INFO = "https://graph.zalo.me/v2.0/me?fields=id,name,picture,birthday,gender&access_token=";
    private static final String END_POINT = "https://openapi.zalo.me/v2.0/oa/message";
    private static final String ZNS_END_POINT = "https://business.openapi.zalo.me/message/template";
    private static final String REFRESH_TOKEN_URL = "https://oauth.zaloapp.com/v4/oa/access_token";
    private static final int INVALID_ACCESS_TOKEN = -216;
    private static final int SUCCESS = 0;

    public ZaloService() {
        APP_CONFIG = BeanUtil.getBean(AppConfig.class);
        objectMapper = new ObjectMapper();
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public String getAccessTokenUser(String oauthCode) throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(GET_ACCESS_TOKEN_URL, String.class,
                this.APP_CONFIG.ZALO_APP_ID,
                this.APP_CONFIG.ZALO_SECRET_APP,
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

        ResponseEntity<String> zaloResponseResponseEntity = postForEntity(END_POINT, msWrapper.toString());
        return zaloResponseResponseEntity.getStatusCode() == HttpStatus.OK;
    }

    public boolean sendZNS(String phone, String templateId, String trackingDataID, JSONObject templateData) {
        JSONObject form = new JSONObject();
        form.put("phone", phone);
        form.put("template_id", templateId);
        form.put("tracking_id", trackingDataID);
        form.put("template_data", templateData);
        ResponseEntity<String> response = postForEntity(ZNS_END_POINT, form.toString());
        return response.getStatusCode() == HttpStatus.OK;
    }

    private  ResponseEntity<String> postForEntity(String entryPoint, String data) {
        ResponseEntity<String> responseEntity = null;
        for (int i = 0; i < 2; i++) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("access_token", APP_CONFIG.ZALO_OA_ACCESS_TOKEN);
            HttpEntity<String> entity = new HttpEntity<>(data, headers);

            responseEntity = restTemplate.postForEntity(entryPoint, entity, String.class);
            JSONObject jsonObject = new JSONObject(responseEntity.getBody());
            int error = jsonObject.getInt("error");
            if (error == SUCCESS) {
                return responseEntity;
            }

            if (error == INVALID_ACCESS_TOKEN) {
                refreshToken();
            }
        }
        return responseEntity;
    }

    public void refreshToken() {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            headers.add("secret_key", APP_CONFIG.ZALO_SECRET_APP);

            MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("refresh_token", APP_CONFIG.ZALO_OA_REFRESH_TOKEN);
            multiValueMap.add("app_id", String.valueOf(APP_CONFIG.ZALO_APP_ID));
            multiValueMap.add("grant_type", "refresh_token");
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(multiValueMap, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(REFRESH_TOKEN_URL, entity, String.class);
            JSONObject response = new JSONObject(responseEntity.getBody());
            int error = response.optInt("error", 0);
            if (error < 0) {
                LOGGER.error(response);
                return;
            }
            String accessToken = response.getString("access_token");
            String refresh_token = response.getString("refresh_token");
            APP_CONFIG.updateAccessToken(refresh_token, accessToken);
            return;
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
