package de.lystx.hytoracloud.networking.test;

import de.lystx.hytoracloud.driver.service.event.DefaultEventService;
import de.lystx.hytoracloud.networking.connection.base.NetworkClient;
import de.lystx.hytoracloud.networking.connection.base.NetworkServer;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.networking.provided.objects.NetworkEventAdapter;
import de.lystx.hytoracloud.networking.provided.objects.NetworkObject;
import de.lystx.hytoracloud.networking.packet.PacketBuffer;
import de.lystx.hytoracloud.networking.packet.manager.PacketListener;
import de.lystx.hytoracloud.networking.packet.impl.response.Response;
import de.lystx.hytoracloud.networking.packet.impl.response.ResponseStatus;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class NettyTest {


    public static void main(String[] args) {
        NetworkServer networkServer = new NetworkServer("0", 1800, new DefaultEventService());
        NetworkClient networkClient = new NetworkClient("0", 1800, new DefaultEventService());

        networkServer.getPacketAdapter().register(new NettyTest());

        new Thread(() -> {
            try {
                networkServer.setup().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                networkClient.setup().connect();

                networkClient.sendPacket(new PacketWhatsTheTime("Luca", UUID.randomUUID(), System.currentTimeMillis()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @PacketListener
    public void handle(AbstractPacket packet) {
        System.out.println("RECEIVED");
        packet.respond(ResponseStatus.OK, new ExampleObject("Lystx", UUID.randomUUID()));
    }


    @AllArgsConstructor @Getter @ToString
    public static class ExampleObject implements NetworkObject {

        private String name;
        private UUID uniqueId;


        @Override
        public void write(PacketBuffer buf) throws IOException {
            buf.writeString(name);
            buf.writeUUID(uniqueId);
        }

        @Override
        public void read(PacketBuffer buf) throws IOException {
            name = buf.readString();
            uniqueId = buf.readUUID();
        }

    }
}
