package de.lystx.hytoracloud.driver.service.cloud.screen;

import de.lystx.hytoracloud.driver.service.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.global.main.ICloudService;
import de.lystx.hytoracloud.driver.service.global.main.ICloudServiceInfo;
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


    private final Map<String, CloudScreen> map;
    private final Map<CloudScreen, List<String>> cachedLines;

    public CloudScreenService() {
        this.map = new HashMap<>();
        this.cachedLines = new HashMap<>();
    }

}
