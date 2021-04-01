package de.lystx.cloudsystem.library.service.server.other.process;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.util.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * This class is used to
 * stop the Services and it's own Thread
 * and also it's {@link CloudScreen}
 */
public class ServiceProviderStop {

    private final CloudLibrary cloudLibrary;
    private final ServerService service;

    public ServiceProviderStop(CloudLibrary cloudLibrary, ServerService service) {
        this.cloudLibrary = cloudLibrary;
        this.service = service;
    }

    /**
     * Stops service
     * @param service
     * @return
     */
    public void stopService(Service service, Consumer<Service> consumer) {
        try {
            CloudScreen screen = this.cloudLibrary.getService(ScreenService.class).getMap().get(service.getName());
            if (screen == null || screen.getServerDir() == null) {
                return;
            }
            screen.getThread().stop();
            screen.getProcess().destroy();
            Scheduler.getInstance().scheduleDelayedTaskAsync(() -> {
                Constants.THREAD_POOL.execute(() -> {
                    if (service.getServiceGroup().isDynamic()) {
                        try {
                            FileUtils.deleteDirectory(screen.getServerDir());
                        } catch (IOException e) {
                            //Ignoring
                        }
                        return;
                    }

                    File cloudAPI = new File(screen.getServerDir(), "plugins/CloudAPI.jar");
                    if (!cloudAPI.exists()) {
                        return;
                    }
                    try {
                        FileUtils.deleteDirectory(new File(screen.getServerDir(), "CLOUD"));
                        FileUtils.forceDelete(cloudAPI);
                    } catch (Exception e) {
                        //Ingoring
                    }

                });
                this.service.notifyStop(service);
                this.cloudLibrary.getService(ScreenService.class).getMap().remove(screen.getScreenName());
                consumer.accept(service);
            }, 5L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
