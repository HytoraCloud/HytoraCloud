package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class CloudHandlerLog implements PacketHandler {

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
    public void handle(Packet packet) {
    }

}
