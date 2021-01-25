package de.lystx.cloudsystem.receiver.manager;

import de.lystx.cloudsystem.receiver.Receiver;
import lombok.Getter;

import java.io.File;

@Getter
public class FileManager {

    private final Receiver receiver;
    private final File directory;
    private final File config;

    public FileManager(Receiver receiver) {
        this.receiver = receiver;

        this.directory = new File("./intern");
        this.config = new File(this.directory, "config.json");

        this.directory.mkdirs();
    }
}
