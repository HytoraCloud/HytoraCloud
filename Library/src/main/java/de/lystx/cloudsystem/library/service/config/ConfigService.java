package de.lystx.cloudsystem.library.service.config;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.file.FileService;
import io.vson.annotation.other.VsonConfigValue;
import io.vson.annotation.other.VsonInstance;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonComment;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter @Setter
public class ConfigService extends CloudService {


    private NetworkConfig networkConfig;
    private ReceiverInfo receiverInfo;
    private VsonObject vsonObject;

    public ConfigService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        System.out.println(this.networkConfig);
        this.reload();
    }

    public void reload() {
        try {
            this.vsonObject = new VsonObject(getCloudLibrary().getService(FileService.class).getConfigFile(), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            if (!getCloudLibrary().getService(FileService.class).getConfigFile().exists()) {
                if (this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
                    VsonObject vsonObject = NetworkConfig.defaultConfig();
                    this.vsonObject.putAll(vsonObject);
                } else {
                    this.vsonObject.putAll(new ReceiverInfo("Receiver-1", "127.0.0.1", 0, false));
                }
                String name = this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM) ? "cloud" : "receiver";
                this.vsonObject
                        .comment("port", VsonComment.BEHIND_VALUE, "Defines the port your " + name + " is running on")
                        .comment("setupDone", VsonComment.BEHIND_VALUE, "Do not touch unless you are told so")
                        .comment("autoUpdater", VsonComment.BEHIND_VALUE, "Toggles AutoUpdating of the " + name)
                        .comment("proxyProtocol", VsonComment.BEHIND_VALUE, "Only used if your server has to send specific information to your hoster")
                        .comment("useWrapper", VsonComment.BEHIND_VALUE, "Enable if you want to use Multiroot")
                        .save();
                this.reload();
                return;
            }
            if (this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
                this.receiverInfo = null;
                this.networkConfig = vsonObject.getAs(NetworkConfig.class);
            } else {
                this.receiverInfo = vsonObject.getAs(ReceiverInfo.class);
                this.networkConfig = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            this.vsonObject.putAll(this.networkConfig);
        } else {
            this.vsonObject.putAll(this.receiverInfo);
        }
        this.vsonObject.save();
    }
}
