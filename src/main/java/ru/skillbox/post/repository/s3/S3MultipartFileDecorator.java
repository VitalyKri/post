package ru.skillbox.post.repository.s3;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class S3MultipartFileDecorator implements MultipartFile {
    private final MultipartFile multipartFile;
    private final InputStream inputStream;
    private final long size;

    public S3MultipartFileDecorator(MultipartFile multipartFile, InputStream inputStream, long size) {
        this.inputStream = inputStream;
        this.multipartFile = multipartFile;
        this.size = size;
    }

    @Override
    public String getName() {
        return multipartFile.getName();
    }

    @Override
    public String getOriginalFilename() {
        return multipartFile.getOriginalFilename();
    }

    @Override
    public String getContentType() {
        return multipartFile.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return multipartFile.isEmpty();
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return inputStream.readAllBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public Resource getResource() {
        return MultipartFile.super.getResource();
    }

    @Deprecated
    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(getInputStream(), Files.newOutputStream(dest));
    }
}
