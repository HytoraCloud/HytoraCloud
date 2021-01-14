package de.lystx.cloudsystem.launcher;

import de.lystx.cloudsystem.CloudSystem;

public class Bootstrap {


    public static void main(String[] args) {
        CloudSystem cloudSystem = new CloudSystem();
       // Runtime.getRuntime().addShutdownHook(new Thread(cloudSystem::shutdown, "cloudsystem_shutdown_hook"));
    }
}
