package com.example.notificationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

  @Value("${firebase.credentials-path}")
  private String credentialsPath;

  @Value("${firebase.project-id}")
  private String projectId;

  @PostConstruct
  public void initialize() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      InputStream serviceAccount =
        new ClassPathResource(credentialsPath).getInputStream();

      FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setProjectId(projectId)
        .build();

      FirebaseApp.initializeApp(options);
    }
  }
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
