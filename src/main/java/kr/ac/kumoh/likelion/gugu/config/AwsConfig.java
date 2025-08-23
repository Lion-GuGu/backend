package kr.ac.kumoh.likelion.gugu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;

@Configuration
public class AwsConfig {
    @Bean
    public PersonalizeRuntimeClient personalizeRuntimeClient() {
        return PersonalizeRuntimeClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}