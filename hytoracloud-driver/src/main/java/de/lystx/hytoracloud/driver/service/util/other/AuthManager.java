package de.lystx.hytoracloud.driver.service.util.other;

import de.lystx.hytoracloud.driver.service.util.random.Random;
import de.lystx.hytoracloud.driver.service.server.other.process.Threader;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@Getter @AllArgsConstructor
public class AuthManager {

    private final File keyFile;

    /**
     * Creates new AuthKey
     */
    public void createKey() {
        if (!this.keyFile.exists()) {
            Threader.getInstance().execute(() -> {
                StringCreator stringCreator = new StringCreator();
                for (int i = 0; i < 100; i++) {
                    stringCreator.append(Random.current().getString().next(200));
                }
                stringCreator.save(keyFile);
            });
        }
    }

    /**
     * Gets current key
     * @return key of "Receiver" or "Manager"
     */
    public String getKey() {
        if (this.keyFile.exists()) {
            StringBuilder stringBuilder = new StringBuilder();

            StringCreator stringCreator = new StringCreator(this.keyFile);

            for (String key : stringCreator) {
                stringBuilder.append(key).append("@");
            }
            return stringBuilder.toString();
        }
        return "null";
    }
}
