package org.jboss.arquillian.testcontainers.api;

import org.testcontainers.containers.output.BaseConsumer;
import org.testcontainers.containers.output.OutputFrame;

public class SimpleLoggingConsumer extends BaseConsumer<SimpleLoggingConsumer> {

    @Override
    public void accept(OutputFrame outputFrame) {
        var outputType = outputFrame.getType();
        var utf8String = outputFrame.getUtf8StringWithoutLineEnding();

        switch (outputType) {
            case END:
                break;
            case STDOUT:
                System.out.println(utf8String);
                break;
            case STDERR:
                System.err.println(utf8String);
                break;
            default:
                throw new IllegalArgumentException("Unexpected outputType " + outputType);
        }
    }
}
