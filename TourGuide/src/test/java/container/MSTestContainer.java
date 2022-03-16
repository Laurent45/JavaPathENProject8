package container;

import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

public class MSTestContainer extends DockerComposeContainer {

    public static final String DOCKER_COMPOSE = "./docker-compose-test.yml";
    private static MSTestContainer microservicesContainerTest;

    private MSTestContainer() {
        super(new File(DOCKER_COMPOSE));
    }

    public static MSTestContainer getInstance() {
        if (microservicesContainerTest == null) {
            microservicesContainerTest = new MSTestContainer();
        }
        return microservicesContainerTest;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {

    }
}
