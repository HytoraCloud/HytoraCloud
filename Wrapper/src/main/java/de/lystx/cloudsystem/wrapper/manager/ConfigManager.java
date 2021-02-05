package de.lystx.cloudsystem.wrapper.manager;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.wrapper.Wrapper;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter @Setter
public class ConfigManager {

    private final Wrapper wrapper;
    private final Document document;

    private boolean setupDone;
    private final String host;
    private final int port;
    private final String name;

    public ConfigManager(Wrapper wrapper) {
        this.wrapper = wrapper;

        this.document = new Document(new File(wrapper.getFileManager().getWrapperDirectory(), "config.json"));

        this.setupDone = document.getBoolean("setupDone", false);
        this.host = document.getString("host", "ipAddressOfLauncher");
        this.port = document.getInteger("port", -1);
        this.name = document.getString("name", "Wrapper-1");
        document.save();
    }
}
