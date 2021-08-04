package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.service.minecraft.other.NetworkInfo;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.CloudDriver;


import lombok.Getter;

import java.util.function.Consumer;


@Getter
public class CloudHandlerRequest implements IPacketHandler {

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
                } else if (driverRequest.equalsIgnoreCase("CLOUD_GET_MODULES")) {
                    driverRequest.createResponse().data(CloudDriver.getInstance().getModuleManager().getModules()).send();
                }
            }
        });
    }

    @Override
    public void handle(IPacket packet) {

    }
}
