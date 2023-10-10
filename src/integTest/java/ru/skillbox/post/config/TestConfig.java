package ru.skillbox.post.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.skillbox.post.api.FeignClientFactory;
import ru.skillbox.post.api.PostGateway;

@Component
@ConfigurationProperties("mytest")
public class TestConfig {

    @Autowired
    private Environment env;

    @Autowired
    private FeignClientFactory feignClientFactory;

    @Bean
    public PostGateway postGateway() {
        String url = env.getProperty("app.post-feignClient.url") + env.getProperty("app.post-feignClient.user.url");
        return feignClientFactory.newFeignClient(PostGateway.class, url);
    }

}
