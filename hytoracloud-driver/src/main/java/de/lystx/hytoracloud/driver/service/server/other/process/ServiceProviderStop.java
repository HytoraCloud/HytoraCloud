package de.lystx.hytoracloud.driver.service.server.other.process;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.service.screen.ServiceOutputScreen;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import de.lystx.hytoracloud.driver.service.server.IServiceManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * This class is used to
 * stop the Services and it's own Thread
 * and also it's {@link ServiceOutputScreen}
 */
public class ServiceProviderStop {

    private final CloudDriver cloudDriver;
    private final IServiceManager service;

    public ServiceProviderStop(CloudDriver cloudDriver, IServiceManager service) {
        this.cloudDriver = cloudDriver;
        this.service = service;
    }

    /**
     * Stops service
     * @param service
     * @return
     */
    public void stopService(Service service, Consumer<Service> consumer) {
        try {
            ServiceOutputScreen screen = this.cloudDriver.getInstance(CloudScreenService.class).getMap().get(service.getName());
            if (screen == null || screen.getServerDir() == null) {
                return;
            }
            screen.getThread().stop();
            screen.getProcess().destroy();
            Scheduler.getInstance().scheduleDelayedTaskAsync(() -> {
                CloudDriver.getInstance().execute(() -> {
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
                this.cloudDriver.getInstance(CloudScreenService.class).getMap().remove(screen.getScreenName());
                consumer.accept(service);
            }, 5L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
