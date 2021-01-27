package de.lystx.cloudsystem.library.result;

import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class Result implements Serializable {

    private final UUID uniqueId;
    private final String result;

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

    public void onDocumentSet(Consumer<Document> consumer) {
        consumer.accept(this.getResult());
    }

    public void onResultSet(Consumer<Result> consumer) {
        consumer.accept(this);
    }
}
