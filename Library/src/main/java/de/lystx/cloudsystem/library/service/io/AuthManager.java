package de.lystx.cloudsystem.library.service.io;

import de.lystx.cloudsystem.library.service.random.Random;
import de.lystx.cloudsystem.library.service.random.RandomString;
import de.lystx.cloudsystem.library.service.server.other.process.Threader;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter @AllArgsConstructor
public class AuthManager {

    private final File keyFile;

    /**
     * Creates new AuthKey
     */
    public void createKey() {
        if (!this.keyFile.exists()) {
            VsonObject document = new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            Threader.getInstance().execute(() -> {
                for (int i = 0; i < 100; i++) {
                    document.append(Random.current().getString().next(3) + i, Random.current().getString().next(200));
                }
                document.save(this.keyFile);
            });
        }
    }

    /**
     * Gets current key
     * @return key of "Receiver" or "Manager"
     */
    public String getKey() {
        if (this.keyFile.exists()) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                VsonObject document = new VsonObject(this.keyFile, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
                for (String key : document.keys()) {
                    stringBuilder.append(document.getString(key)).append("@");
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "null";
    }
}
