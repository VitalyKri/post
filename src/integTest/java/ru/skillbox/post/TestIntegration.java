package ru.skillbox.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.skillbox.post.api.PostGateway;
import ru.skillbox.post.config.TestConstants;
import ru.skillbox.post.docker.AbstractTestContainerSetup;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TestIntegration extends AbstractTestContainerSetup {

    @Autowired
    PostGateway postGateway;

    protected ClassLoader classloader;

    @Container
    private static DockerComposeContainer<AbstractTestContainerSetup> dockerComposeContainer = AbstractTestContainerSetup.getInstance();

    @BeforeEach
    protected void init() {
        classloader = Thread.currentThread().getContextClassLoader();
    }

    @Test
    @Order(1)
    void createUser()  throws IOException {

        try (var mdFile = classloader.getResourceAsStream("static/aws-sdk-java.md");
             var photo1 = classloader.getResourceAsStream("static/Дата.png");
             var photo2 = classloader.getResourceAsStream("static/Дата2.png")){
            MockMultipartFile mdFileMock = new MockMultipartFile("sourceContent", "пост.md", MediaType.TEXT_MARKDOWN_VALUE, mdFile);
            MockMultipartFile photo1FileMock = new MockMultipartFile("photos", "photos1.md", MediaType.IMAGE_PNG_VALUE, photo1);
            MockMultipartFile photo2FileMock = new MockMultipartFile("photos", "photos2.md", MediaType.IMAGE_PNG_VALUE, photo2);
            ResponseEntity responseEntity = postGateway.savePost(List.of(photo1FileMock, photo2FileMock), mdFileMock, TestConstants.POSTDTO);
            assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        }

    }


}
