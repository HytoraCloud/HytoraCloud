package de.lystx.cloudsystem.receiver.manager;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class ConfigManager {

    private final Receiver receiver;
    private boolean setupDone;
    private String host;
    private Integer port;
    private String name;

    public ConfigManager(Receiver receiver) {
        this.receiver = receiver;
        Document document = new Document(receiver.getFileManager().getConfig());
        this.setupDone = document.getBoolean("setupDone", false);
        this.host = document.getString("host", "");
        this.name = document.getString("name", "");
        this.port = document.getInteger("port", 0);
    }

    public void update()  {
        Document document = new Document(receiver.getFileManager().getConfig());
        document.append("host", this.host);
        document.append("port", this.port);
        document.append("name", this.name);
        document.append("setupDone", this.setupDone);
        document.save();
    }
}
