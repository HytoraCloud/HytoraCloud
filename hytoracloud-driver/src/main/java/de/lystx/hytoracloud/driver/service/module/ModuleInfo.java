package de.lystx.hytoracloud.driver.service.module;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class ModuleInfo implements Serializable, ThunderObject {

    private String name;
    private String author;
    private String version;
    private List<String> commands;
    private ModuleCopyType copyType;


    private File file;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(name);
        buf.writeString(author);
        buf.writeString(version);

        buf.writeInt(commands.size());
        for (String command : commands) {
            buf.writeString(command);
        }
        buf.writeEnum(copyType);
    }

    @Override
    public void read(PacketBuffer buf) {

        name = buf.readString();
        author = buf.readString();
        version = buf.readString();
        int size = buf.readInt();
        commands = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            commands.add(buf.readString());
        }
        copyType = buf.readEnum(ModuleCopyType.class);
    }
}
