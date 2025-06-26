/*
 * Copyright The Arquillian Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.arquillian.testcontainers.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which will check if container engine is available and if not throw an exception. By default, this will throw an
 * {@link AssertionError}. However, you can define the type of exception to throw. The exception <strong>must</strong>
 * have a string or no-arg constructor.
 *
 * <pre>
 *     {@code @TestcontainersRequired(TestAbortedException.class)}
 * </pre>
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface TestcontainersRequired {

    /**
     * The type of the exception to throw. The exception must have a public string constructor.
     * <p>
     * By default, this throws an {@link AssertionError}. However, this could throw, for example, a
     * {@code TestAbortedException} to act as an Assumption error.
     * </p>
     *
     * @return the exception type to throw if container engine is not available
     */
    Class<? extends Throwable> value() default AssertionError.class;
}
