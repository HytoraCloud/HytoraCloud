package de.lystx.cloudsystem.library.service.config;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.impl.MessageConfig;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.config.impl.fallback.FallbackConfig;
import de.lystx.cloudsystem.library.service.config.impl.labymod.LabyModConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.Motd;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.TabList;
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
    int tries = 0;

    public ConfigService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.reload();
    }

    public void reload() {
        try {
            tries += 1;
            this.vsonObject = new VsonObject(getCloudLibrary().getService(FileService.class).getConfigFile(), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            if (!getCloudLibrary().getService(FileService.class).getConfigFile().exists()) {
                if (this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
                    VsonObject vsonObject = NetworkConfig.defaultConfig();
                    this.vsonObject.putAll(vsonObject);
                } else {
                    this.vsonObject.putAll(new ReceiverInfo("Receiver-1", "127.0.0.1", 0, false));
                }
                this.vsonObject.save();
                if (tries <= 10) {
                    this.reload();
                } else {
                    System.out.println("[Config] Tried 10 times to reload!");
                }
                return;
            }
            if (this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
                this.receiverInfo = null;
                if (!this.vsonObject.has("proxyStartPort")) {
                    this.vsonObject.append("proxyStartPort", 25565);
                }
                if (!this.vsonObject.has("serverStartPort")) {
                    this.vsonObject.append("serverStartPort", 30000);
                }
                this.vsonObject.save();

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
