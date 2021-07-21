package de.lystx.hytoracloud.driver.utils.utillity;

import de.lystx.hytoracloud.driver.CloudDriver;
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
            CloudDriver.getInstance().execute(() -> {
                StringCreator stringCreator = new StringCreator();
                for (int i = 0; i < 100; i++) {
                    stringCreator.append(new RandomString(200).next());
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
        return null;
    }
}
