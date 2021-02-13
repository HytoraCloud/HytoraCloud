package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;

public class AuthManager {

    private final File keyFile;

    public AuthManager(File keyFile) {
        this.keyFile = keyFile;
    }

    public void createKey() {
        if (!this.keyFile.exists()) {
            Document document = new Document();
            document.append("key", UUID.randomUUID().toString() + "_" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString());
            document.save(this.keyFile);
        }
    }

    public String getKey() {
        if (!this.keyFile.exists()) {
            return "null";
        }
        Document document = Document.fromFile(this.keyFile);
        return document.getString("key");
    }
}
