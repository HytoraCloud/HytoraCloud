package de.lystx.cloudsystem.library.service.console.progressbar;

import java.util.function.Consumer;

public class DelegatingProgressBarConsumer implements ProgressBarConsumer {

    private final int maxProgressLength;
    private final Consumer<String> consumer;

    public DelegatingProgressBarConsumer(Consumer<String> consumer) {
        this(consumer, TerminalUtils.getTerminalWidth());
    }

    public DelegatingProgressBarConsumer(Consumer<String> consumer, int maxProgressLength) {
        this.maxProgressLength = maxProgressLength;
        this.consumer = consumer;
    }

    @Override
    public int getMaxRenderedLength() {
        return maxProgressLength;
    }

    @Override
    public void accept(String str) {
        this.consumer.accept(str);
    }

    @Override
    public void close() {
        //NOOP
    }
}
