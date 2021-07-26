package de.lystx.hytoracloud.launcher.bootstrap;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.launcher.global.manager.Manager;
import de.lystx.hytoracloud.launcher.receiver.Receiver;

import java.io.File;
import java.io.IOException;

public class Bootstrap {

    public static void main(String[] args)  {
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

        String javaVersion = System.getProperty("java.version");
        if (!javaVersion.startsWith("1.8")) {
            System.out.println("\n" +
                    "  _____                                       _   _ _     _      \n" +
                    " |_   _|                                     | | (_) |   | |     \n" +
                    "   | |  _ __   ___ ___  _ __ ___  _ __   __ _| |_ _| |__ | | ___ \n" +
                    "   | | | '_ \\ / __/ _ \\| '_ ` _ \\| '_ \\ / _` | __| | '_ \\| |/ _ \\\n" +
                    "  _| |_| | | | (_| (_) | | | | | | |_) | (_| | |_| | |_) | |  __/\n" +
                    " |_____|_| |_|\\___\\___/|_| |_| |_| .__/ \\__,_|\\__|_|_.__/|_|\\___|\n" +
                    "                                 | |                             \n" +
                    "                                 |_|                             ");
            System.out.println("---------------------------------------------------");
            System.out.println("[ERROR] HytoraCloud does not work with your Java version!");
            System.out.println("[ERROR] Your Version : " + javaVersion);
            System.out.println("[ERROR] Recommended Version : " + "1.8.x");
            System.exit(1);
            return;
        }
        CloudProcess cloudInstance = new CloudProcess(CloudType.NONE);
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", cloudInstance);
    }
}
