package de.lystx.cloudsystem.library.service.serverselector.sign;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.file.FileService;
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

    public void load() {
        this.cloudSigns = new LinkedList<>();
        if (!this.layOutFile.exists()) {
            new DefaultSignLayout().save(this.layOutFile);
            this.signLayOut = new SignLayOut(new DefaultSignLayout());
        } else {
            try {
                this.signLayOut = new SignLayOut(new VsonObject(this.layOutFile, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            VsonObject config = new VsonObject(this.signFile, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            if (!this.signFile.exists()) {
                config.save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(CloudSign cloudSign) {
        this.cloudSigns.add(cloudSign);
    }

    public void remove(CloudSign cloudSign) {
        this.cloudSigns.remove(cloudSign);
    }


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
        } catch (NullPointerException ignored) {}
    }

    public CloudSign getCloudSign(int x, int y, int z, String world) {
        for (CloudSign cloudSign : this.cloudSigns) {
            if (cloudSign.getX() == x && cloudSign.getY() == y && cloudSign.getZ() == z && world.equalsIgnoreCase(cloudSign.getWorld())) {
                return cloudSign;
            }
        }
        return null;
    }

}
