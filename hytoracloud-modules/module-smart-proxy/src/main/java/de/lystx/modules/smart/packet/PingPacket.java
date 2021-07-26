package de.lystx.modules.smart.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Getter
public class PingPacket extends MinecraftPacket {


    private int clientVersion;
    private String hostName;
    private int port;
    private int state;

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeUnsignedVarInt(clientVersion);
        buf.writeString(hostName);
        buf.writeUnsignedVarInt(port);
        buf.writeUnsignedVarInt(state);

    }

    @Override
    public void read(PacketBuffer buf) {
        clientVersion = buf.readUnsignedVarInt();
        hostName = buf.readString();
        port = buf.readUnsignedVarInt();
        state = buf.readUnsignedVarInt();
    }

    @Override
    public void handle() {

    }

    @Override
    public String toString() {
        return "PingPacket{" +
                "clientVersion=" + clientVersion +
                ", hostName='" + hostName + '\'' +
                ", port=" + port +
                ", state=" + state +
                '}';
    }
}
