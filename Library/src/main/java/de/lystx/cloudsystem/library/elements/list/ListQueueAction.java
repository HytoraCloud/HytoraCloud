package de.lystx.cloudsystem.library.elements.list;

import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ListQueueAction<T> {

    private final CloudList<T> list;
    private final Consumer<ListQueueAction<T>> runnable;

    public ListQueueAction(CloudList<T> list, Consumer<ListQueueAction<T>> runnable) {
        this.list = list;
        this.runnable = runnable;
    }

    public void queue() {
        this.queue(null);
    }

    public void queue(Consumer<CloudList<T>> listConsumer) {
        this.runnable.accept(this);
        if (listConsumer != null) {
            listConsumer.accept(this.list);
        }
    }

    public void queueAsync() {
        Executors.newSingleThreadExecutor().submit((Runnable) this::queue);
    }
}
