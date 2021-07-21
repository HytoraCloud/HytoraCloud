package de.lystx.hytoracloud.driver.cloudservices.cloud.output;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverScreenRequest;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.util.*;

@Getter
@ICloudServiceInfo(
        name = "ScreenService",
        type = CloudServiceType.MANAGING,
        description = {
                "This class stores all the Screens for the Services that are online",
                "And removes and add new / or old ones!"
        },
        version = 1.0
)
public class ServiceOutputService implements ICloudService {


    private final Map<String, ServiceOutput> map;
    private final Map<ServiceOutput, List<String>> cachedLines;

    public ServiceOutputService() {
        this.map = new HashMap<>();
        this.cachedLines = new HashMap<>();
    }

    public ServiceOutput getOrRequest(String name) {
        if (this.map.containsKey(name)) {
            return this.map.get(name);
        } else {
            PacketReceiverScreenRequest screenRequest = new PacketReceiverScreenRequest(name);
            Component component = screenRequest.toReply(CloudDriver.getInstance().getConnection());
            List<String> cachedLines = component.get("lines");
            ServiceOutput serviceOutput = new ServiceOutput(null, null, null, name);
            serviceOutput.setCachedLines(cachedLines);
            return serviceOutput;
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
