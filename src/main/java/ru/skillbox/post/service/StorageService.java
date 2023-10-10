package ru.skillbox.post.service;


import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.post.repository.s3.S3Repository;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
public class StorageService {
    private final S3Repository repository;

    public Collection<String> getAllFilesByPrefix(String prefix) {
        return repository.listKeys(prefix);
    }

    public String getEndpoint() {
        return repository.getEndpoint();
    }

    public void putObject(String key, MultipartFile file) throws IOException {
        try (var inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            repository.put(key, inputStream, metadata);
        }
    }

    public void deleteObject(String key) {
        repository.delete(key);
    }


}
