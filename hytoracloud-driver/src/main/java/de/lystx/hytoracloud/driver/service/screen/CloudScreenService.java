package de.lystx.hytoracloud.driver.service.screen;

import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import lombok.Getter;

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
public class CloudScreenService implements ICloudService {


    private final Map<String, ServiceOutputScreen> map;
    private final Map<ServiceOutputScreen, List<String>> cachedLines;

    public CloudScreenService() {
        this.map = new HashMap<>();
        this.cachedLines = new HashMap<>();
    }

}
