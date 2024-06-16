package com.egatrap.partage.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {

    @Value("${spring.cloud.gcp.storage.credentials.location}")
    private String location;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projId;

    @Bean
    public Storage storage() throws IOException {

        ClassPathResource resource = new ClassPathResource("feat-demo-426606-dd04c2fce439.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
        String projectId = projId;
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }
}
