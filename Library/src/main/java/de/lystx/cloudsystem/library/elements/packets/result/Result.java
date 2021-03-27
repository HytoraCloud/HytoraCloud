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

    /**
     * Uses this as {@link VsonObject} to
     * untree the object of this class
     * and get the object as a specified GenericType
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getResultAs(Class<T> tClass) {
        return ((VsonObject) this.getResult()).getAs(tClass);
    }

    /**
     * Accepts Consumer if Error was found
     * @param consumer
     * @return
     */
    public Result<R> onError(Consumer<Throwable> consumer) {
        if (this.throwable != null) {
            consumer.accept(this.throwable);
        }
        return this;
    }

    /**
     * Accepts Consumer if Document was returned
     * @param consumer
     * @return
     */
    public Result<R> onDocumentSet(Consumer<VsonObject> consumer) {
        if (this.getResult() instanceof VsonObject) {
            consumer.accept(((VsonObject) this.getResult()));
        }
        return this;
    }

    /**
     * Accepts Consumer if Result was returned
     * @param consumer
     * @return
     */
    public Result<R> onResultSet(Consumer<Result<R>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * Accepts Consumer if GenericObject was found
     * @param consumer
     * @return
     */
    public Result<R> onReceiveObject(Consumer<R> consumer) {
        consumer.accept(this.getResult());
        return this;
    }

}
