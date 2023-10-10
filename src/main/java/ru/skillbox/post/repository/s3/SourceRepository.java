package ru.skillbox.post.repository.s3;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.stereotype.Component;
import ru.skillbox.post.properties.S3Properties;

@Component
public class SourceRepository extends S3Repository {
    public SourceRepository(AmazonS3 s3Client, S3Properties properties) {
        super(s3Client, properties, properties.getBucketSource());
    }
}
