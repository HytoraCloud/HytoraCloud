package de.lystx.hytoracloud.receiver.impl.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.config.IConfigManager;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.registry.CloudServiceInfo;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.net.InetAddress;
import java.util.UUID;

@CloudServiceInfo(
        name = "Receiver-ConfigService",
        description = "Used to store the receiver data",
        version = 1.0
)
@Getter @Setter
public class ConfigService implements ICloudService, IConfigManager {

    private final File configFile;

    private NetworkConfig networkConfig;

    private IReceiver receiver;

    public ConfigService() {
        CloudDriver.getInstance().getServiceRegistry().registerService(this);

        this.configFile = CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getConfigFile();
        this.receiver = null;
        this.reload();
    }


    @Override
    public ProxyConfig getProxyConfig() {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @SneakyThrows
    @Override
    public void reload() {

        JsonDocument json = new JsonDocument(configFile);
        if (!this.configFile.exists()) {
            json.append("host", "127.0.0.1");
            json.append("port", 1401);
            json.append("name", "DefaultReceiver");
            json.append("memory", 1024);
            json.append("uniqueId", UUID.randomUUID().toString());
            json.save();
        }

        this.receiver = new ReceiverObject(json.getString("host"), json.getInteger("port"), json.getString("name"), UUID.fromString(json.getString("uniqueId")), json.getLong("memory"), false, InetAddress.getLocalHost());
    }

    @Override
    public JsonDocument getJson() {
        return new JsonDocument(this.configFile);
    }

    @Override
    public void save() {

        JsonDocument json = new JsonDocument(configFile);
        json.append("host", this.receiver.getHost());
        json.append("port", this.receiver.getPort());
        json.append("name", this.receiver.getName());
        json.append("uniqueId", this.receiver.getUniqueId().toString());
        json.append("memory", this.receiver.getMaxMemory());
        json.save();
    }
}
