package de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl;

import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.DefaultNettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Getter @AllArgsConstructor @NoArgsConstructor
public class PacketClientCredentials extends NettyPacket {

    private INettyClient networkClient;

    @Override
    public void read(PacketBuffer buf) throws IOException {
        networkClient = JsonDocument.fromClass(buf.readString(), DefaultNettyClient.class);
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        this.networkClient.setChannel(null);
        buf.writeString(JsonDocument.toString(networkClient));
    }
}
