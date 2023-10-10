package ru.skillbox.post.docker;


import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AbstractTestContainerSetup extends DockerComposeContainer<AbstractTestContainerSetup> {

    private final static String pathDockerCompose = "cicd/docker/docker-compose.yml";
    private static DockerComposeContainer<AbstractTestContainerSetup> dockerComposeContainer;

    public AbstractTestContainerSetup(File... composeFiles) {
        super(composeFiles);
    }

    public static synchronized DockerComposeContainer<AbstractTestContainerSetup> getInstance() {

        if (dockerComposeContainer == null) {
            dockerComposeContainer = new
                    AbstractTestContainerSetup(new File(pathDockerCompose))
                    .withLocalCompose(true)
                    .withPull(false)
                    .withTailChildContainers(false)
                    .withServices("liquidbase_blog", "post")
                    .withEnv(System.getenv())
                    .waitingFor("post", Wait.forHttp("/post").forPort(9898));
        }
        return dockerComposeContainer;
    }

    @Override
    public void stop() {
    }

}
