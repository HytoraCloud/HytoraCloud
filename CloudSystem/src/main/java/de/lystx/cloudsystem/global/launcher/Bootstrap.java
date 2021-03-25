package de.lystx.cloudsystem.global.launcher;

import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.enums.CloudType;

public class Bootstrap {

    public static void main(String[] args)  {
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
        CloudInstance cloudInstance = new CloudInstance(CloudType.NONE);
    }
}
