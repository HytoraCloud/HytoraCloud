package de.lystx.hytoracloud.driver.elements.packets.in;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * This packet is used to send
 * a message to the CloudSystem
 *
 * and you can choose if you only
 * want it to show up in the log or
 * in the console if you change showUpInConsole
 * to false
 */
@Getter @AllArgsConstructor
public class PacketInLogMessage extends PacketCommunication implements Serializable {

    private String prefix;
    private String message;
    private boolean showUpInConsole;

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        prefix = buf.readString();
        message = buf.readString();
        showUpInConsole = buf.readBoolean();
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(prefix);
        buf.writeString(message);
        buf.writeBoolean(showUpInConsole);
    }
}
