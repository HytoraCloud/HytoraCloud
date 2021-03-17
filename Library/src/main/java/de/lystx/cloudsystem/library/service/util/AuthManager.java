package de.lystx.cloudsystem.library.service.util;

import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Getter @AllArgsConstructor
public class AuthManager {

    private final File keyFile;

    /**
     * Creates new AuthKey
     */
    public void createKey() {
        if (!this.keyFile.exists()) {
            VsonObject document = new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            document.append("key", UUID.randomUUID().toString() + "_" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString());
            document.save(this.keyFile);
        }
    }

    /**
     * Gets current key
     * @return key of "Receiver" or "Manager"
     */
    public String getKey() {
        if (this.keyFile.exists()) {
            try {
                VsonObject document = new VsonObject(this.keyFile, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
                return document.getString("key");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "null";
    }
}
