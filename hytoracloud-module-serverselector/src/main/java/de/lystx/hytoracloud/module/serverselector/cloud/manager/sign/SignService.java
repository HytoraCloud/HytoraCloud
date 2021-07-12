package de.lystx.hytoracloud.module.serverselector.cloud.manager.sign;

import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.module.serverselector.cloud.ModuleSelector;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base.CloudSign;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.layout.SignLayOut;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.layout.DefaultSignLayout;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
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

        this.signDirectory = new File(ModuleSelector.getInstance().getModuleDirectory(), "signs/"); this.signDirectory.mkdirs();
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
            this.signLayOut = new SignLayOut(new DefaultSignLayout(VsonSettings.CREATE_FILE_IF_NOT_EXIST));

            this.signLayOut.getDocument().save(this.layOutFile);
        } else {
            try {
                this.signLayOut = new SignLayOut(new VsonObject(this.layOutFile, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!this.signFile.exists()) {
            new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST).save(this.signFile);
        }

        try {
            VsonObject config = new VsonObject(this.signFile, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            for (String key : config.keys()) {
                CloudSign sign = config.getObject(key, CloudSign.class);
                this.cloudSigns.add(sign);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves signs
     */
    public void save() {
        try {
            try {
                VsonObject config = new VsonObject(this.signFile, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
                config.clear();
                for (CloudSign cloudSign : this.cloudSigns) {
                    config.append(cloudSign.getUuid().toString(), cloudSign);
                }
                config.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
