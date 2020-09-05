package com.ttk.file.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@RefreshScope
@ConfigurationProperties(prefix = "application")
public class FileConfig {
    private Map<String, String> file;
}
