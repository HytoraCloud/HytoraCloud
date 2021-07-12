package de.lystx.hytoracloud.driver.cloudservices.cloud;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.component.RepliableComponent;

import java.util.function.Consumer;

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

    private final HytoraServer hytoraServer;

    public NetworkService() {

        this.hytoraServer = new HytoraServer(CloudDriver.getInstance().getNetworkConfig().getPort());

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
