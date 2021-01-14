package de.lystx.cloudsystem.library.service.screen;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ScreenService extends CloudService {
    
    

    private final ConcurrentHashMap<String, CloudScreen> map;

    public ScreenService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.map = new ConcurrentHashMap<>();
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
