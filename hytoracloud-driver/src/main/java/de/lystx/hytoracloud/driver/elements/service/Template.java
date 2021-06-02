package de.lystx.hytoracloud.driver.elements.service;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.other.FileService;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@Getter @AllArgsConstructor
public class Template implements ThunderObject {

    /**
     * The name of the template (e.g. "Lobby")
     */
    private String name;

    /**
     * The directory of this template (e.g. "local/templates/Lobby/")
     */
    private String directory;

    public Template(String group, String name, boolean b) {
        this(name, CloudDriver.getInstance().getInstance(FileService.class).getTemplatesDirectory() + "/" + group + "/" + name + "/");
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(name);
        buf.writeString(directory);
    }

    @Override
    public void read(PacketBuffer buf) {
        name = buf.readString();
        directory = buf.readString();
    }

    public File getDirectory() {
        return new File(directory);
    }
}
