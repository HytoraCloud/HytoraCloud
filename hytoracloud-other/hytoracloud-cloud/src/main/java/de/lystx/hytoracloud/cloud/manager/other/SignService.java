package de.lystx.hytoracloud.cloud.manager.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.registry.CloudServiceInfo;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.serverselector.sign.CloudSign;
import de.lystx.hytoracloud.driver.serverselector.sign.SignConfiguration;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import lombok.Getter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@link SignService} manages all CloudSigns
 * it manages your signLayout and your existing signs
 * and deletes or saves {@link CloudSign}s whenever
 * you want it
 */
@Getter
@CloudServiceInfo(
        name = "SignService",
        description = {
                "This service is used to manage all CloudSigns",
                "Store and load them"
        },
        version = 1.0
)
public class SignService implements ICloudService {

    private final File layOutFile;
    private final File signFile;
    private List<CloudSign> cloudSigns;

    private SignConfiguration configuration;
    private final File signDirectory;

    public SignService() {

        this.signDirectory = new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getDatabaseDirectory(), "signs/"); this.signDirectory.mkdirs();
        this.signFile = new File(this.signDirectory, "signs.json");
        this.layOutFile = new File(this.signDirectory, "signLayouts.json");

        this.reload();
    }

    /**
     * Loads LayOuts and signs
     */
    public void reload() {
        this.cloudSigns = new LinkedList<>();
        if (!this.layOutFile.exists()) {
            this.configuration = SignConfiguration.createDefault();

            JsonObject<?> jsonObject = JsonObject.gson();
            jsonObject.append(this.configuration);
            jsonObject.remove("knockBackConfig");
            jsonObject.append("knockBackConfig", this.configuration.getKnockBackConfig());

            jsonObject.save(this.layOutFile);
        } else {
            this.configuration = new JsonDocument(this.layOutFile).getAs(SignConfiguration.class);
        }
        if (!this.signFile.exists()) {
            new JsonDocument(this.signFile).save(this.signFile);
        }
        JsonDocument config = new JsonDocument(this.signFile);
        this.cloudSigns = new LinkedList<>(config.keySet(CloudSign.class));
    }

    /**
     * Saves signs
     */
    public void save() {
        try {
            JsonDocument config = new JsonDocument(this.signFile);
            config.clear();
            for (CloudSign cloudSign : this.cloudSigns) {
                config.append(cloudSign.getUuid().toString(), cloudSign);
            }
            config.save();
        } catch (NullPointerException e) {
            //Ignored because of Receiver
        }
    }

    /**
     * Gets CloudSign by values
     * @param x
     * @param y
     * @param z
     * @param world
     * @return cloudSign
     */
    public CloudSign getCloudSign(int x, int y, int z, String world) {
        for (CloudSign cloudSign : this.cloudSigns) {
            if (cloudSign.getX() == x && cloudSign.getY() == y && cloudSign.getZ() == z && world.equalsIgnoreCase(cloudSign.getWorld())) {
                return cloudSign;
            }
        }
        return null;
    }

}
