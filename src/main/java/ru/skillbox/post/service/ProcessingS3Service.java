package ru.skillbox.post.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.post.entity.Post;
import ru.skillbox.post.repository.s3.ContentRepository;
import ru.skillbox.post.repository.s3.S3MultipartFileDecorator;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@Slf4j
public class ProcessingS3Service {

    private static final String EXT_MD = ".md";
    public static final String EXT_HTML = ".html";

    private final ConvertingService convertingService;
    private final StorageService contentStorage;

    @Autowired
    public ProcessingS3Service(ConvertingService convertingService, ContentRepository contentRepository) {
        this.convertingService = convertingService;
        this.contentStorage = new StorageService(contentRepository);

    }

    public void savePostAsHtml(Post post, MultipartFile sourceMdFile) {
        processSaveFile(post, Collections.singletonList(sourceMdFile));
    }

    private void processSaveFile(Post post, List<MultipartFile> multipartFileCollection) {
        var convertTasks = multipartFileCollection.stream()
                .filter(this::isMdFile)
                .map((file) -> processConvertFile(post, file))
                .map(CompletableFuture::supplyAsync)
                .toList();
        CompletableFuture.allOf(convertTasks.toArray(new CompletableFuture[0])).join();
    }

    private boolean isMdFile(MultipartFile multipartFile) {
        return Objects.equals(multipartFile.getContentType(), MediaType.TEXT_MARKDOWN_VALUE);
    }

    public Supplier<Boolean> processConvertFile(Post post, MultipartFile multipartFile) {
        return () -> {
            try {
                try (var inputStream = multipartFile.getInputStream()) {
                    PipedInputStream pipedInputStream = null;
                    PipedOutputStream pipedOutputStream = null;
                    try  {
                        pipedOutputStream = new PipedOutputStream();
                        pipedInputStream = new PipedInputStream(pipedOutputStream);
                        PipedOutputStream finalPipedOutputStream = pipedOutputStream;
                        Runnable produse = () -> {
                            try {
                                convertingService.convertMdToHtml(inputStream, finalPipedOutputStream);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        };
                        new Thread(produse).start();
                        S3MultipartFileDecorator s3MultipartFileDecorator = new S3MultipartFileDecorator(multipartFile, pipedInputStream, multipartFile.getSize());
                        contentStorage.putObject(linkedHtmlFile(post.getId().toString()), s3MultipartFileDecorator);
                    } finally {
                        pipedInputStream.close();
                        pipedOutputStream.close();
                    }
                    return true;
                }
            } catch (IOException e) {
                log.error("Can not convert md file {}", post.getId(), e);
            }
            return false;
        };
    }

    private String linkedHtmlFile(String name) {
        return FilenameUtils.removeExtension(name) + EXT_HTML;
    }

}
