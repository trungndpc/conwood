package vn.conwood.admin.config;

import org.apache.logging.log4j.LogManager;
import org.ini4j.Ini;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class AppConfig {

    public AppConfig() {
        try{
            Ini ini = getFileConfig();
            ZALO_OA_ACCESS_TOKEN = ini.get("oa", "access_token");
            ZALO_OA_REFRESH_TOKEN = ini.get("oa", "refresh_token");
        }catch (Exception e) {
            LogManager.getLogger().error(e.getMessage());
        }
    }

    private Ini getFileConfig() throws IOException {
        File file = new File("../conf/config.ini");
        return new Ini(file);
    }

    @Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }

    @Value("${domain.admin}")
    public String ADMIN_DOMAIN;

    @Value("${domain.client}")
    public String CLIENT_DOMAIN;

    @Value("${zalo.appid}")
    public long ZALO_APP_ID;

    @Value("${zalo.authen.secret}")
    public String ZALO_SECRET_APP;

    @Value("${zalo.oaid}")
    public long ZALO_OA_ID;

    public String ZALO_OA_ACCESS_TOKEN;

    public String  ZALO_OA_REFRESH_TOKEN;

    public String getAuthenZaloUrl() {
        return String.format("https://oauth.zaloapp.com/v3/auth?app_id=%d&state=insee", ZALO_APP_ID);
    }

    public void updateAccessToken(String new_refresh_token, String access_token) {
        try {
            Ini ini = getFileConfig();
            ini.put("oa", "refresh_token", new_refresh_token);
            ini.put("oa", "access_token", access_token);
            ZALO_OA_REFRESH_TOKEN = new_refresh_token;
            ZALO_OA_ACCESS_TOKEN = access_token;
            ini.store();
        }catch (Exception e) {
            LogManager.getLogger().error(e.getMessage());
        }
    }
}
