package ru.skillbox.post.controller;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.skillbox.post.AbstractTest;
import ru.skillbox.post.dto.PostDto;
import ru.skillbox.post.entity.User;
import ru.skillbox.post.repository.jpa.UserDao;
import ru.skillbox.post.repository.s3.ContentRepository;
import ru.skillbox.post.repository.s3.SourceRepository;

import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
class PostControllerTest extends AbstractTest {

    @Autowired
    ContentRepository contentRepository;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    UserDao userDao;

    @Test
    @Order(0)
    void createUser() {
        User build = User.builder().id(TestConstants.POSTDTO.getUserId()).build();
        User save = userDao.save(build);
        TestConstants.POSTDTO.setUserId(save.getId());
    }

    @Test
    @Order(1)
    void createPost() throws Exception {
        AmazonS3 mock = Mockito.mock(AmazonS3.class);
        Mockito.when(mock.listObjectsV2(Mockito.any(), Mockito.any())).thenReturn(new ListObjectsV2Result());
        Mockito.when(mock.putObject(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new PutObjectResult());
        ReflectionTestUtils.setField(contentRepository, "amazonS3", mock);
        ReflectionTestUtils.setField(sourceRepository, "amazonS3", mock);
        String postJSON = objectMapper.writeValueAsString(TestConstants.POSTDTO);
        try (var mdFile = classloader.getResourceAsStream("static/aws-sdk-java.md");
             var photo1 = classloader.getResourceAsStream("static/Дата.png");
             var photo2 = classloader.getResourceAsStream("static/Дата2.png")) {
            MockMultipartFile mdFileMock = new MockMultipartFile("sourceContent", "пост.md", MediaType.TEXT_MARKDOWN_VALUE, mdFile);
            MockMultipartFile photo1FileMock = new MockMultipartFile("photos", "photos1.md", MediaType.IMAGE_PNG_VALUE, photo1);
            MockMultipartFile photo2FileMock = new MockMultipartFile("photos", "photos2.md", MediaType.IMAGE_PNG_VALUE, photo2);
            MockMultipartFile postDto = new MockMultipartFile("info", "", MediaType.APPLICATION_JSON_VALUE, postJSON.getBytes(StandardCharsets.UTF_8));

            mockMvc.perform(MockMvcRequestBuilders.multipart("/post")
                            .file(mdFileMock)
                            .file(photo1FileMock)
                            .file(photo2FileMock)
                            .file(postDto)
                    )
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }
    }


    @Test
    @Order(2)
    void getPost() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/post/" + TestConstants.POSTDTO.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        String contentAsString = perform.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        PostDto postDto = objectMapper.readValue(contentAsString, PostDto.class);
        Assertions.assertEquals(postDto.getId(), TestConstants.POSTDTO.getId());
        Assertions.assertEquals(postDto.getTitle(), TestConstants.POSTDTO.getTitle());
        Assertions.assertEquals(postDto.getDescription(), TestConstants.POSTDTO.getDescription());
        Assertions.assertNotEquals(postDto.getLinkContent(), "");
        Assertions.assertEquals(postDto.getPictures().size(), TestConstants.POSTDTO.getPictures().size());
    }
}


