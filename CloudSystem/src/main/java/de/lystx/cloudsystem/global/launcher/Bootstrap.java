package de.lystx.cloudsystem.global.launcher;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.receiver.Receiver;

import java.util.Arrays;

public class Bootstrap {


    public static void main(String[] args) {
        if (Arrays.toString(args).toLowerCase().contains("--receiver")) {
            Receiver receiver = new Receiver();
        } else {
            CloudSystem cloudSystem = new CloudSystem();
        }
    }
}
