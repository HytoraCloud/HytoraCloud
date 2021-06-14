package de.lystx.hytoracloud.driver.service.server.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import de.lystx.hytoracloud.driver.service.screen.CloudScreen;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class ServiceStopper {

    private final Service service;

    public ServiceStopper(Service service) {
        this.service = service;
    }


    /**
     * Stops a given service
     *
     * @param consumer consumer to accept after finish
     * @throws Exception if something goes wrong
     */
    public void stop(Consumer<Service> consumer) throws Exception {

        CloudScreen screen = CloudDriver.getInstance().getInstance(CloudScreenService.class).getMap().get(service.getName());
        if (screen == null || screen.getServerDir() == null) {
            throw new IllegalAccessException("Tried to stop a Service (" + service.getName() + ") which has no screen!");
        }
        screen.getThread().stop();
        screen.getProcess().destroy();

        CloudDriver.getInstance().getServiceManager().notifyStop(service);
        CloudDriver.getInstance().getInstance(CloudScreenService.class).getMap().remove(screen.getScreenName());

        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
            if (service.getServiceGroup().isDynamic()) {
                try {
                    FileUtils.deleteDirectory(screen.getServerDir());
                } catch (IOException e) {
                    //Ignoring
                }
                consumer.accept(service);
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
                //Ignoring
            }
            consumer.accept(service);
        }, 5L);
    }
}
