package ru.skillbox.post;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AbstractTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ClassLoader classloader;

    @BeforeEach
    protected void init() {
        classloader = Thread.currentThread().getContextClassLoader();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

}
