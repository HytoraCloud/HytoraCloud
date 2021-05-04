
import de.lystx.cloudsystem.library.network.connection.client.NetworkClient;
import de.lystx.cloudsystem.library.network.packet.PacketAdapting;
import de.lystx.cloudsystem.library.network.packet.PacketListener;
import de.lystx.cloudsystem.library.network.packet.PacketRegistry;
import de.lystx.cloudsystem.library.network.packet.response.Response;
import de.lystx.cloudsystem.library.network.packet.response.ResponseStatus;
import de.lystx.cloudsystem.library.network.connection.server.NetworkServer;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import io.netty.channel.Channel;
import io.vson.elements.object.VsonObject;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyTest {


    public static void main(String[] args) {
        new NettyTest().subMain(args);
    }


    public void subMain(String[] args) {

        Logger logger = Logger.getLogger("netty");
        logger.setLevel(Level.OFF);


        NetworkServer networkServer = new NetworkServer("0", 1800, new VsonObject(), logger);
        NetworkClient networkClient = new NetworkClient("0", 1800, logger);

        PacketRegistry.getInstance().register(PacketWhatsTheTime.class);
        PacketAdapting.getInstance().register(this);

        networkClient.registerChannelActiveAdapter(new Consumer<Channel>() {
            @Override
            public void accept(Channel channel) {
                PacketWhatsTheTime packetWhatsTheTime = new PacketWhatsTheTime("Ich", UUID.randomUUID(), System.currentTimeMillis());
                networkClient.flushPacket(packetWhatsTheTime, new Consumer<Response>() {
                    @Override
                    public void accept(Response response) {
                        System.out.println(response.getStatus());
                        System.out.println(response.getMessage());
                    }
                });
            }
        });

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    @PacketListener
    public void handle(PacketWhatsTheTime packet) {
        Scheduler.getInstance().scheduleDelayedTask(() -> {
            packet.respond(ResponseStatus.SUCESS, "message");
        }, 60L);
    }
}
