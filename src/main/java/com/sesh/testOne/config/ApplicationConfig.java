package com.sesh.testOne.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
//@PropertySource("application.properties")
@EnableConfigurationProperties
@ConfigurationProperties("big-file-reader")
public class ApplicationConfig {
    String originalFile;
    String sortedFile;
    String helpersFilePath;
    Integer lineLimit;
    Integer amountLine;
    Integer maxLineLength;
    Integer sortSize;
}
