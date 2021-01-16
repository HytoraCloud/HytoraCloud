package de.lystx.cloudsystem.library.service.screen;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import lombok.Getter;

import java.util.*;

@Getter
public class ScreenService extends CloudService {


    private final Map<String, CloudScreen> map;
    private final Map<CloudScreen, List<String>> cachedLines;

    public ScreenService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.map = new HashMap<>();
        this.cachedLines = new HashMap<>();
    }
    public CloudScreen getScreenByName(String name) {
        return map.getOrDefault(name, null);
    }

    public void unregisterScreen(CloudScreen screen) {
        map.remove(screen.getName());
    }

    public void registerScreen(CloudScreen screen, String name) {
        map.put(name, screen);
    }

}
