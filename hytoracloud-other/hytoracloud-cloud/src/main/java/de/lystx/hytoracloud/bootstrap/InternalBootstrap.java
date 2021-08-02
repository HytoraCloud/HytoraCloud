package de.lystx.hytoracloud.bootstrap;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.global.manager.Manager;
import de.lystx.hytoracloud.receiver.Receiver;

import java.io.File;
import java.io.IOException;

public class InternalBootstrap {

    public static void main(String[] args) {
        try {
            File bat = new File("start.bat");
            File sh = new File("start.sh");
            if (System.getProperty("os.name").contains("Windows")) {
                if (!bat.exists()) {
                    Utils.copyResource("/implements/start/start.bat", bat.toString(), Bootstrap.class);
                    System.exit(0);
                }
            } else {
                if (!sh.exists()) {
                    Utils.copyResource("/implements/start/start.sh", sh.toString(), Bootstrap.class);
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--process=MANAGER")) {
                Manager manager = new Manager();
                return;
            } else if (arg.equalsIgnoreCase("--process=CLOUD")) {
                CloudSystem cloudSystem = new CloudSystem();
                return;
            } else if (arg.equalsIgnoreCase("--process=RECEIVER")) {
                Receiver receiver = new Receiver();
                return;
            }
        }
        CloudProcess cloudInstance = new CloudProcess(CloudType.NONE);
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", cloudInstance);
    }
}
