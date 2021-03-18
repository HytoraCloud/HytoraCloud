package de.lystx.cloudsystem.library.service.serverselector.sign;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import de.lystx.cloudsystem.library.service.serverselector.sign.layout.DefaultSignLayout;
import de.lystx.cloudsystem.library.service.serverselector.sign.layout.SignLayOut;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Getter
public class SignService extends CloudService {

    private final File layOutFile;
    private final File signFile;
    private List<CloudSign> cloudSigns;

    private SignLayOut signLayOut;

    public SignService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);
        this.layOutFile = cloudLibrary.getService(FileService.class).getSignLayoutFile();
        this.signFile = cloudLibrary.getService(FileService.class).getSignsFile();

        this.load();
        this.loadSigns();
    }

    /**
     * Loads LayOuts and signs
     */
    public void load() {
        this.cloudSigns = new LinkedList<>();
        if (!this.layOutFile.exists()) {
            new DefaultSignLayout(VsonSettings.CREATE_FILE_IF_NOT_EXIST).save(this.layOutFile);
            this.signLayOut = new SignLayOut(new DefaultSignLayout(VsonSettings.CREATE_FILE_IF_NOT_EXIST));
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
    }

    /**
     * Loads signs
     */
    public void loadSigns() {
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
