package de.lystx.hytoracloud.driver.elements.packets.both.service;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketConnectServer extends PacketCommunication {

    private String name;
    private String server;

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        name = buf.readString();
        server = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(name);
        buf.writeString(server);
    }
}
