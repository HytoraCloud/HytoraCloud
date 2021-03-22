package de.lystx.cloudsystem.library.elements.packets.result;

import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter
public class Result<R> implements Serializable {

    private final UUID uniqueId;
    private final R result;
    private Throwable throwable;

    public Result(UUID uniqueId, R result) {
        this.uniqueId = uniqueId;
        this.result = result;
        this.throwable = null;
    }

    public VsonObject getDocument() {
        return (VsonObject) this.result;
    }

    public <T> T getResultAs(Class<T> tClass) {
        return this.getDocument().getAs(tClass);
    }

    public Result<R> onError(Consumer<Throwable> consumer) {
        if (this.throwable != null) {
            consumer.accept(this.throwable);
        }
        return this;
    }

    public Result<R> onDocumentSet(Consumer<VsonObject> consumer) {
        if (this.getResult() instanceof VsonObject) {
            consumer.accept(this.getDocument());
        }
        return this;
    }

    public Result<R> onResultSet(Consumer<Result<R>> consumer) {
        consumer.accept(this);
        return this;
    }

    public Result<R> onReceiveObject(Consumer<R> consumer) {
        consumer.accept(this.getResult());
        return this;
    }

}
