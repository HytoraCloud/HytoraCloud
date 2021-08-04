import de.lystx.hytoracloud.driver.connection.protocol.hytora.connection.client.NetworkClient;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.connection.server.NetworkServer;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

import java.util.function.Consumer;

public class HytoraTest {

    public static void main(String[] args) {


        NetworkServer networkServer = new NetworkServer(2020);
        NetworkClient networkClient = new NetworkClient("127.0.0.1", 2020);

        final long[] start = {0L};

        networkServer.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                System.out.println(packet);
                System.out.println(System.currentTimeMillis() - start[0]);
            }
        });


        networkClient.loginHandler(new Consumer<NetworkClient>() {
            @Override
            public void accept(NetworkClient networkClient) {
                System.out.println("CONNECT");
                start[0] = System.currentTimeMillis();

                networkClient.sendPacket(new ExamplePacket("Luca", 156));
            }
        });

        networkServer.createConnection();
        networkClient.createConnection();


    }
}
