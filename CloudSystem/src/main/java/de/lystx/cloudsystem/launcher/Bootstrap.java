package de.lystx.cloudsystem.launcher;

import de.lystx.cloudsystem.CloudSystem;
import org.fusesource.jansi.AnsiConsole;

public class Bootstrap {


    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        CloudSystem cloudSystem = new CloudSystem();
    }
}
