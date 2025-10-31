package com.questionanswer.notifications.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app.notifications")
@Data
@Validated
public class NotificationProperties {
    private CleanupProperties cleanup;

    @Data
    public static class CleanupProperties {
        private boolean enabled = true;

        @NotBlank
        private String cron = "0 0 2 * * ?";

        @Min(1)
        private int retentionDays = 30;
    }
}
