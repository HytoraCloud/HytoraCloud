package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.driver.commons.minecraft.other.NetworkInfo;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import lombok.Getter;

import java.util.function.Consumer;


@Getter
public class CloudHandlerRequest implements PacketHandler {

    private final CloudSystem cloudSystem;

    public CloudHandlerRequest(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;

        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                if (driverRequest.equalsIgnoreCase("CLOUD_GET_TPS")) {
                    driverRequest.createResponse().data(new NetworkInfo().formatTps(CloudDriver.getInstance().getTicksPerSecond().getTPS())).send();
                } else if (driverRequest.equalsIgnoreCase("CLOUD_GET_PING")) {
                    driverRequest.createResponse().data(System.currentTimeMillis()).send();
                }
            }
        });
    }

    @Override
    public void handle(Packet packet) {

    }
}
