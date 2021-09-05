package de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.response;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter @AllArgsConstructor
public class PacketRespond extends NettyPacket {

    private ResponseStatus responseStatus;
    private JsonDocument document;

    @Override
    public void read(PacketBuffer buf) throws IOException {
        responseStatus = buf.readEnum(ResponseStatus.class);
        document = new JsonDocument(buf.readString());
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeEnum(responseStatus);
        buf.writeString(document.toString());
    }
}
