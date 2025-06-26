/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.arquillian.testcontainers.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.testcontainers.api.Testcontainer;
import org.jboss.arquillian.testcontainers.api.TestcontainersRequired;
import org.jboss.arquillian.testcontainers.test.common.SimpleTestContainer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.TestAbortedException;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@TestcontainersRequired(TestAbortedException.class)
@RunAsClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManualContainerTest {

    @Testcontainer(false)
    private static SimpleTestContainer container;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @AfterAll
    public static void shutdownContainer() {
        container.close();
    }

    @Test
    @Order(1)
    public void testContainerInjected() {
        Assertions.assertNotNull(container, "Expected the container to be injected.");
        Assertions.assertFalse(container.isRunning(), "Expected the container to not be running");
    }

    @Test
    @Order(2)
    public void startContainer() {
        container.start();
        Assertions.assertTrue(container.isRunning(), "Expected the container to be running");
    }
}
