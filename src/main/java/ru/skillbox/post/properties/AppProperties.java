package ru.skillbox.post.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private String cssStyleLocation;
    private List<String> convertParams;
}
