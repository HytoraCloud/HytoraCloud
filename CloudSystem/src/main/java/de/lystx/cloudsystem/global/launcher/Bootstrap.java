package de.lystx.cloudsystem.global.launcher;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.receiver.Receiver;

import java.util.Arrays;

public class Bootstrap {

    //TODO: forEach zu for Schleifen

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


        if (Arrays.toString(args).toLowerCase().contains("--receiver")) {
            Receiver receiver = new Receiver();
        } else {
            CloudSystem cloudSystem = new CloudSystem();
        }
    }
}
