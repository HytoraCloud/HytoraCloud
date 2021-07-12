package de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutput;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class ServiceStopper {

    private final IService IService;

    public ServiceStopper(IService IService) {
        this.IService = IService;
    }


    /**
     * Stops a given service
     *
     * @param consumer consumer to accept after finish
     * @throws Exception if something goes wrong
     */
    public void stop(Consumer<IService> consumer) throws Exception {

        ServiceOutput screen = CloudDriver.getInstance().getInstance(ServiceOutputService.class).getMap().get(IService.getName());
        if (screen == null || screen.getDirectory() == null) {
            throw new IllegalAccessException("Tried to stop a Service (" + IService.getName() + ") which has no screen!");
        }
        screen.getThread().stop();
        screen.getProcess().destroy();

        CloudDriver.getInstance().getServiceManager().notifyStop(IService);
        CloudDriver.getInstance().getInstance(ServiceOutputService.class).getMap().remove(screen.getServiceName());

        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
            if (IService.getGroup().isDynamic()) {
                try {
                    FileUtils.deleteDirectory(screen.getDirectory());
                } catch (IOException e) {
                    //Ignoring
                }
                consumer.accept(IService);
                return;
            }

            File cloudAPI = new File(screen.getDirectory(), "plugins/CloudAPI.jar");
            if (!cloudAPI.exists()) {
                return;
            }
            try {
                FileUtils.deleteDirectory(new File(screen.getDirectory(), "CLOUD"));
                FileUtils.forceDelete(cloudAPI);
            } catch (Exception e) {
                //Ignoring
            }
            consumer.accept(IService);
        }, 5L);
    }
}
