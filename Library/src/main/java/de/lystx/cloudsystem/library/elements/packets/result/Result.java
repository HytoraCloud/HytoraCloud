package de.lystx.cloudsystem.library.elements.packets.result;

import de.lystx.cloudsystem.library.elements.other.Document;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter
public class Result implements Serializable {

    private final UUID uniqueId;
    private final VsonObject result;
    private boolean error;

    public Result(UUID uniqueId, VsonObject result) {
        this.uniqueId = uniqueId;
        this.result = result;
        this.error = false;
    }

    public VsonObject getDocument() {
        return this.result;
    }

    public <T> T getResultAs(Class<T> tClass) {
        return this.getDocument().getAs(tClass);
    }

    public Result onError(Runnable runnable) {
        if (this.error) {
            runnable.run();
        }
        return this;
    }

    public Result onDocumentSet(Consumer<VsonObject> consumer) {
        consumer.accept(this.getDocument());
        return this;
    }

    public Result onResultSet(Consumer<Result> consumer) {
        consumer.accept(this);
        return this;
    }

}
