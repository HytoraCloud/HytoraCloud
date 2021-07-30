package de.lystx.hytoracloud.launcher.cloud.impl.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import de.lystx.hytoracloud.networking.connection.server.NetworkServer;

@Getter
@ICloudServiceInfo(
        name = "NetworkService",
        type = CloudServiceType.NETWORK,
        description = {
                "This service manages the networking stuff and starts the server for all clients"
        },
        version = 2.0
)
public class NetworkService implements ICloudService {

    private final NetworkServer hytoraServer;

    public NetworkService() {

        this.hytoraServer = new NetworkServer(CloudDriver.getInstance().getNetworkConfig().getPort());

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
