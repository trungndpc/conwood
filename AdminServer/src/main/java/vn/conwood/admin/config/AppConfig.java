package vn.conwood.admin.config;

import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

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

    @Value("${zalo.oa.access_token}")
    public String ZALO_OA_ACCESS_TOKEN;

    @Value("${zalo.oaid}")
    public long ZALO_OA_ID;

    public String getAuthenZaloUrl() {
        return String.format("https://oauth.zaloapp.com/v3/auth?app_id=%d&state=insee", ZALO_APP_ID);
    }
}
