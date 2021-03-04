package de.lystx.cloudsystem.library.service.screen;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import lombok.Getter;

import java.util.*;

@Getter
public class ScreenService extends CloudService {


    private final Map<String, CloudScreen> map;
    private final Map<CloudScreen, List<String>> cachedLines;

    /**
     * Sets up Cached lines and Register Map
     * @param cloudLibrary
     * @param name
     * @param type
     */
    public ScreenService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);
        this.map = new HashMap<>();
        this.cachedLines = new HashMap<>();
    }

}
