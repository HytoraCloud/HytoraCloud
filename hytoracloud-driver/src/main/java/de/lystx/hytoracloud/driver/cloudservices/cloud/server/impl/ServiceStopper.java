package de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutput;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.function.Consumer;

@AllArgsConstructor @Getter
public class ServiceStopper {

    private final IService service;

    /**
     * Stops a given service
     *
     * @param consumer consumer to accept after finish
     * @throws Exception if something goes wrong
     */
    public void stop(Consumer<IService> consumer) throws Exception {

        ServiceOutput screen = CloudDriver.getInstance().getInstance(ServiceOutputService.class).getMap().get(service.getName());
        if (screen == null || screen.getDirectory() == null) {
            CloudDriver.getInstance().messageCloud("ERROR", "§cCan't stop §e" + service.getName() + " §cbecause no Screen with Process was found!");
            return;
        }

        Process process = screen.getProcess();
        Thread thread = screen.getThread();

        //thread.stop(); //Stopping thread
        process.destroy(); //Shutting down process

        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {

            //It's dynamic delete whole directory
            if (service.getGroup().isDynamic()) {
                Utils.deleteFolder(screen.getDirectory());
                consumer.accept(service);
                return;
            }

            //Static only remove Cloud-Folder and CloudBridge
            File bridgeFile = new File(screen.getDirectory(), "plugins/hytoracloud-bridge.jar");
            if (!bridgeFile.exists()) {
                return;
            }

            Utils.deleteFolder(new File(screen.getDirectory(), "CLOUD"));
            if (bridgeFile.delete()) {
                consumer.accept(service);
            }
        }, 5L);
    }
}
