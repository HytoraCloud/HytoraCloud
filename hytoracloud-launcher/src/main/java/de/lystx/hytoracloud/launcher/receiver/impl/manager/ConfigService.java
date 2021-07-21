package de.lystx.hytoracloud.launcher.receiver.impl.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.commons.implementations.ReceiverObject;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.UUID;

@ICloudServiceInfo(
        name = "Receiver-ConfigService",
        type = CloudServiceType.CONFIG,
        description = "Used to store the receiver data",
        version = 1.0
)
@Getter @Setter
public class ConfigService implements ICloudService {

    private final File configFile;

    private IReceiver receiver;

    public ConfigService() {
        this.configFile = CloudDriver.getInstance().getInstance(FileService.class).getConfigFile();
        this.receiver = null;
        this.reload();
    }


    @Override
    public void reload() {

        JsonEntity json = new JsonEntity(configFile);
        if (!this.configFile.exists()) {
            json.append("host", "127.0.0.1");
            json.append("port", 1401);
            json.append("name", "DefaultReceiver");
            json.append("uniqueId", UUID.randomUUID().toString());
            json.save();
        }

        this.receiver = new ReceiverObject(json.getString("host"), json.getInteger("port"), json.getString("name"), UUID.fromString(json.getString("uniqueId")));
    }

    @Override
    public void save() {

        JsonEntity json = new JsonEntity(configFile);
        json.append("host", this.receiver.getHost());
        json.append("port", this.receiver.getPort());
        json.append("name", this.receiver.getName());
        json.append("uniqueId", this.receiver.getUniqueId().toString());
        json.save();
    }
}
