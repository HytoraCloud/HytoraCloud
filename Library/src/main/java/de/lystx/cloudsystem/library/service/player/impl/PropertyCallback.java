package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.other.ValueConsumer;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import lombok.Getter;

@Getter
public class PropertyCallback {

    private final CloudPlayer cloudPlayer;
    private final String key;

    public PropertyCallback(CloudPlayer cloudPlayer, String key) {
        this.cloudPlayer = cloudPlayer;
        this.key = key;
    }

    public PropertyCallback save(CloudExecutor executor) {
        this.cloudPlayer.update(executor);
        return this;
    }

    public PropertyCallback run(ValueConsumer<SerializableDocument> documentConsumer) {
        SerializableDocument main = this.cloudPlayer.getProperties();
        SerializableDocument document = documentConsumer.consume(this.getProperty());
        main.append(this.key, document);
        this.cloudPlayer.setProperties(main);
        return this;
    }

    public SerializableDocument getProperty() {
        Document document = this.cloudPlayer.getProperties().toDocument();
        return SerializableDocument.fromDocument(document.getDocument(this.key));
    }
}
