package ru.skillbox.post.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.s3")
@Data
public class S3Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketSource;
    private String bucketContent;
    private String signer;
}
