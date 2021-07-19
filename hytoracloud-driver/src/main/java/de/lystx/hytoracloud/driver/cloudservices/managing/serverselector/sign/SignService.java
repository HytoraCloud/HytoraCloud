package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.base.CloudSign;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.layout.SignLayOut;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.layout.DefaultSignLayout;
import utillity.JsonEntity;
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
@ICloudServiceInfo(
        name = "SignService",
        type = CloudServiceType.CONFIG,
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

    private SignLayOut signLayOut;
    private final File signDirectory;

    public SignService() {

        this.signDirectory = new File(CloudDriver.getInstance().getInstance(FileService.class).getDatabaseDirectory(), "signs/"); this.signDirectory.mkdirs();
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
            this.signLayOut = new SignLayOut(new DefaultSignLayout());

            this.signLayOut.getDocument().save(this.layOutFile);
        } else {
            this.signLayOut = new SignLayOut(new JsonEntity(this.layOutFile));
        }
        if (!this.signFile.exists()) {
            new JsonEntity(this.signFile).save(this.signFile);
        }
        JsonEntity config = new JsonEntity(this.signFile);
        this.cloudSigns = new LinkedList<>(config.keys(CloudSign.class));
    }

    /**
     * Saves signs
     */
    public void save() {
        try {
            JsonEntity config = new JsonEntity(this.signFile);
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
