/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.arquillian.testcontainers.test.multi;

import java.io.File;
import java.nio.file.Files;

import org.jboss.arquillian.container.spi.ContainerRegistry;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testcontainers.api.TestContainer;
import org.jboss.arquillian.testcontainers.api.TestContainerInstances;
import org.jboss.arquillian.testcontainers.api.TestContainerLifecycle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

@RunAsClient
@ExtendWith(ArquillianExtension.class)
@TestContainer({ MultiContainerTest.MyPostgreSQLContainer.class, MultiContainerTest.MyWildflyContainer.class })
public class MultiContainerTest {
    private static final Network NETWORK = Network.newNetwork();

    @ArquillianResource
    private TestContainerInstances instances;

    @ArquillianResource
    @Postgresql
    private MyPostgreSQLContainer postgresql;

    @ArquillianResource
    @Wildfly
    private MyWildflyContainer wildfly;

    @Postgresql
    public static class MyPostgreSQLContainer extends PostgreSQLContainer<MyPostgreSQLContainer> {
        public MyPostgreSQLContainer() {
            super("postgres:16-alpine");
        }

        @Override
        protected void configure() {
            withNetwork(NETWORK).withNetworkAliases("postgres").withDatabaseName("test");// .withInitScript("init.sql");
            super.configure();
        }
    }

    @Wildfly
    public static class MyWildflyContainer extends GenericContainer<MyWildflyContainer>
            implements TestContainerLifecycle {
        public MyWildflyContainer() {
            // this is not wildfly, off course
            super("fedora:40");
        }

        @Override
        protected void configure() {
            withNetwork(NETWORK).withEnv("DB_SERVER", "postgres").withEnv("DB_PORT", "5432").withCommand("/bin/bash",
                    "-c", "while true; do sleep 1; done");
            super.configure();
        }

        @Override
        public void beforeStart(TestContainerInstances testContainerInstances, ContainerRegistry registry) {
            MyPostgreSQLContainer postgres = (MyPostgreSQLContainer) testContainerInstances.get(Postgresql.class);
            withEnv("DB_USER", postgres.getUsername()).withEnv("DB_PASSWORD", postgres.getPassword());
        }
    }

    @Test
    public void testContainersInjected() throws Exception {
        Assertions.assertNotNull(postgresql, "Expected the container to be injected.");
        Assertions.assertNotNull(wildfly, "Expected the container to be injected.");
        Assertions.assertTrue(postgresql.isRunning(), "Expected the container to be running");
        Assertions.assertTrue(wildfly.isRunning(), "Expected the container to be running");
        Assertions.assertEquals(2, instances.all().size());
        Assertions.assertEquals(wildfly, instances.get(Wildfly.class));

        wildfly.execInContainer("/bin/bash", "-c", "echo $DB_SERVER:$DB_PORT:$DB_USER:$DB_PASSWORD > /tmp/env.txt");
        wildfly.copyFileFromContainer("/tmp/env.txt", "target/env.txt");

        Assertions.assertEquals("postgres:5432:test:test",
                Files.readAllLines(new File("target/env.txt").toPath()).get(0));
    }

}
