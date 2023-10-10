package ru.skillbox.post.repository.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import ru.skillbox.post.properties.S3Properties;

import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class S3Repository {

    protected final AmazonS3 amazonS3;
    private final S3Properties properties;
    private final String bucketName;


    public Collection<String> listKeys(String prefix) {
        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucketName, prefix);
        return listObjectsV2Result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }


    public void delete(String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    public void put(String key, InputStream inputStream, ObjectMetadata metadata) {
        amazonS3.putObject(bucketName, key, inputStream, metadata);
    }

    public Optional<S3Object> get(String key) {
        try {
            return Optional.of(amazonS3.getObject(bucketName, key));
        } catch (AmazonServiceException exception) {
            return Optional.empty();
        }
    }

    public String getEndpoint(){
        return properties.getEndpoint().concat("/").concat(bucketName);
    }

}
