package de.lystx.cloudsystem.library.service.serverselector.sign;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import de.lystx.cloudsystem.library.service.serverselector.sign.layout.DefaultSignLayout;
import de.lystx.cloudsystem.library.service.serverselector.sign.layout.SignLayOut;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Getter
public class SignService extends CloudService {

    private final File layOutFile;
    private final File signFile;
    private List<CloudSign> cloudSigns;

    private SignLayOut signLayOut;

    public SignService(CloudLibrary cloudLibrary, String name, Type type) {
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
            this.signLayOut = new SignLayOut(new Document(this.layOutFile));
        }

        Document config = new Document(this.signFile);
        if (!this.signFile.exists()) {
            config.save();
        }
    }

    public void add(CloudSign cloudSign) {
        this.cloudSigns.add(cloudSign);
    }

    public void remove(CloudSign cloudSign) {
        this.cloudSigns.remove(cloudSign);
    }


    public void loadSigns() {
        Document config = new Document(this.signFile);
        for (String key : config.keys()) {
            CloudSign sign = config.getObject(key, CloudSign.class);
            this.cloudSigns.add(sign);
        }
    }

    public void save() {
        try {
            Document document = new Document(this.signFile);
            document.clear();
            for (CloudSign cloudSign : this.cloudSigns) {
                document.append(cloudSign.getUuid().toString(), cloudSign);
            }
            document.save();
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
