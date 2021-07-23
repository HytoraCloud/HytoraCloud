package de.lystx.hytoracloud.driver.cloudservices.cloud.output;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverScreenRequest;
import lombok.Getter;
import lombok.Setter;
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

    @Setter
    private Map<String, List<String>> cachedLines;

    public ServiceOutputService() {
        this.map = new HashMap<>();
        this.cachedLines = new HashMap<>();
    }

    /**
     * Caches a line for a screen
     *
     * @param screen the screen
     * @param line the line
     */
    public void cache(String screen, String line) {
        List<String> list = this.cachedLines.get(screen);
        if (list == null) {
            list = new LinkedList<>();
        }

        list.add(line);
        this.cachedLines.put(screen, list);
    }

    /**
     * Gets a {@link ServiceOutput} if its on this instance
     * by just getting it from the hashmap cache
     *
     * But if it doesn't run on this instance but on another {@link de.lystx.hytoracloud.driver.commons.receiver.IReceiver}
     * for example, then it sends a request to get all cached lines of this screen and transfers them to the new screen
     *
     * @param name the name
     * @return screen output
     */
    public ServiceOutput getOrRequest(String name) {
        if (this.map.containsKey(name)) {
            return this.map.get(name);
        } else {

            //Creating packet request and waiting for component-response
            PacketReceiverScreenRequest screenRequest = new PacketReceiverScreenRequest(name);
            Component component = screenRequest.toReply(CloudDriver.getInstance().getConnection(), 100);

            if (component != null) {
                //Request successful
                List<String> cachedLines = component.get("lines"); //Requested cached lines
                List<String> list = this.cachedLines.get(name); //This lines cached

                //New screen instance
                ServiceOutput serviceOutput = new ServiceOutput(null, null, null, name);

                if (cachedLines == null) {
                    serviceOutput.setCachedLines(list == null ? new LinkedList<>() : list);
                } else {
                    if (cachedLines.size() > (list == null ? new LinkedList<>().size() : list.size())) {
                        serviceOutput.setCachedLines(cachedLines);
                    } else {
                        serviceOutput.setCachedLines(list == null ? cachedLines : list);
                    }
                }
                return serviceOutput;
            } else {
                //Request failed no screen provided
                //Returning null...
                return null;
            }
        }
    }

    @Override
    public void reload() {
    }

    @Override
    public void save() {
    }
}
