package de.lystx.cloudsystem.library.service.server.other.process;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ServiceProviderStop {

    private final CloudLibrary cloudLibrary;
    private final ServerService service;

    public ServiceProviderStop(CloudLibrary cloudLibrary, ServerService service) {
        this.cloudLibrary = cloudLibrary;
        this.service = service;
    }


    public boolean stopService(Service service) {
        String serverName = service.getName();
        CloudScreen screen = this.cloudLibrary.getService(ScreenService.class).getScreenByName(serverName);
        if (screen == null) {
            cloudLibrary.getConsole().getLogger().sendMessage("ERROR", "§cCouldnt stop Server §e" + serverName + " §cbecause no Screen was provided");
            return false;
        }
        screen.getProcess().destroy();
        screen.getThread().stop();

        if (service.getServiceGroup().isDynamic()) {
            try {
                for (File file : Objects.requireNonNull(screen.getServerDir().listFiles())) {
                    if (file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                    } else {
                        FileUtils.forceDelete(file);
                    }
                }
            } catch (IOException e) {}
        }
        this.service.notifyStop(service);
        this.cloudLibrary.getService(ScreenService.class).unregisterScreen(screen);
        return true;
    }
}
