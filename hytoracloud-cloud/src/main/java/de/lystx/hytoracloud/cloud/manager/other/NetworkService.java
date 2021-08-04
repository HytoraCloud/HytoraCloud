package de.lystx.hytoracloud.cloud.manager.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.registry.CloudServiceInfo;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.connection.server.NetworkServer;

@Getter
@CloudServiceInfo(
        name = "NetworkService",
        description = {
                "This service manages the networking stuff and starts the server for all clients"
        },
        version = 2.0
)
public class NetworkService implements ICloudService {

    private final NetworkServer hytoraServer;

    public NetworkService() {

        this.hytoraServer = new NetworkServer(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getPort());

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "connection", this.hytoraServer);
        this.hytoraServer.createConnection();

    }

    /**
     * Stops server
     */
    public void shutdown() {
        this.hytoraServer.close();
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
