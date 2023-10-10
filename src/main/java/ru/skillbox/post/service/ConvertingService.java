package ru.skillbox.post.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import ru.skillbox.post.properties.AppProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class ConvertingService {
    private final AppProperties appProperties;

    public void convertMdToHtml(InputStream md, OutputStream html) throws IOException {
        var pandocProcessor = new ProcessBuilder(appProperties.getConvertParams()).start();
        try (var output = pandocProcessor.getOutputStream()) {
            IOUtils.copy(md, output);
            output.flush();
        }
        try (var input = pandocProcessor.getInputStream()) {
            int copysize = IOUtils.copy(input, html);
            html.flush();
        }
    }
}
