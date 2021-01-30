package de.lystx.cloudsystem.library.result;

import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter
public class Result implements Serializable {

    private final UUID uniqueId;
    private final String result;
    private long action;

    public Result(UUID uniqueId, Document result) {
        this.uniqueId = uniqueId;
        this.result = result.toString();
    }

    public Document getResult() {
        return new Document(this.result);
    }

    public <T> T getResultAs(Class<T> tClass) {
        return this.getResult().getObject(this.getResult().getJsonObject(), tClass);
    }

    public Result onDocumentSet(Consumer<Document> consumer) {
        consumer.accept(this.getResult());
        return this;
    }

    public Result onResultSet(Consumer<Result> consumer) {
        consumer.accept(this);
        return this;
    }

}
