package de.lystx.hytoracloud.driver.service.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.util.Utils;
import io.thunder.Thunder;
import io.thunder.connection.base.ThunderServer;
import io.thunder.utils.objects.ThunderOption;
import lombok.Getter;

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

    private final ThunderServer thunderServer;

    public NetworkService() {
        this.thunderServer = Thunder.createServer();

        this.thunderServer.option(ThunderOption.IGNORE_HANDSHAKE_IF_FAILED, true);

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "connection", this.thunderServer);
        this.thunderServer.start(CloudDriver.getInstance().getInstance(ConfigService.class).getNetworkConfig().getPort()).perform();
    }

    /**
     * Stops server
     */
    public void shutdown() {
        this.thunderServer.disconnect();
    }
}
