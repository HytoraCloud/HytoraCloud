package de.lystx.cloudsystem.launcher;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.receiver.Receiver;

import java.util.Arrays;

public class Bootstrap {


    public static void main(String[] args) {
        try {
            if (args[0].equalsIgnoreCase("--receiver")) {
                Receiver receiver = new Receiver();
            }
        } catch (IndexOutOfBoundsException e) {
            CloudSystem cloudSystem = new CloudSystem();}
       // Runtime.getRuntime().addShutdownHook(new Thread(cloudSystem::shutdown, "cloudsystem_shutdown_hook"));
    }
}
