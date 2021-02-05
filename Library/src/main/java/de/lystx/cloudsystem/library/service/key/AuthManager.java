package de.lystx.cloudsystem.library.service.key;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

import java.io.File;
import java.util.function.Consumer;

public class AuthManager {

    private final File keyFile;
    private final KeyGenerator keyGenerator;

    public AuthManager(File keyFile) {
        this.keyFile = keyFile;
        this.keyGenerator = new KeyGenerator();
    }

    public void checkKeys(String masterKey, String wrapperKey, Consumer<Boolean> callBack) {
        if (masterKey.equals(" ") || wrapperKey.equals(" ")) {
            callBack.accept(false);
        }
        if (masterKey.equals("null") || wrapperKey.equals("null")) {
            callBack.accept(false);
        }
        callBack.accept(masterKey.equalsIgnoreCase(wrapperKey));
    }

    public void createKey(CloudConsole cloudConsole) {
        if (!this.keyFile.exists()) {
            cloudConsole.getLogger().sendMessage("INFO", "Creating new Wrapper key! Please don't exit this process");
            Document document = new Document();
            this.keyGenerator.create(key -> document.append("KEY", key), 3);
            document.save(this.keyFile);
        }
    }

    public String getKey() {
        if (!this.keyFile.exists()) {
            return "null";
        }
        Document document = Document.fromFile(this.keyFile);
        return document.getString("KEY");
    }
}
