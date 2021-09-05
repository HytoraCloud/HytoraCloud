package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;



import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class CloudHandlerLog implements IPacketHandler {

    private final CloudSystem cloudSystem;


    public CloudHandlerLog(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                if (driverRequest.equalsIgnoreCase("SERVICE_GET_LOG_URL")) {

                    IService service = CloudDriver.getInstance().getServiceManager().getCachedObject(document.getString("name"));
                    driverRequest.createResponse().data(service.getLogUrl().pullValue()).send();
                }
            }
        });
    }

    @Override
    public void handle(IPacket packet) {
    }

}
