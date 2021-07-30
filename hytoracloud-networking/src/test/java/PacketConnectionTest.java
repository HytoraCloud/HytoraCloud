
import de.lystx.hytoracloud.networking.connection.client.ClientListener;
import de.lystx.hytoracloud.networking.connection.client.NetworkClient;
import de.lystx.hytoracloud.networking.connection.server.NetworkServer;
import de.lystx.hytoracloud.networking.elements.component.ComponentSender;
import de.lystx.hytoracloud.networking.elements.other.NetworkLogin;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import packets.ExamplePacket;

import java.net.InetSocketAddress;


public class PacketConnectionTest {


    public static void main(String[] args) {


        //Creating server and client objects
        NetworkServer hytoraServer = new NetworkServer(1401);
        NetworkClient networkClient = new NetworkClient("127.0.0.1", 1401);

        //Registering packet handler for incoming packets
        hytoraServer.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
                if (packet instanceof ExamplePacket) {
                    ExamplePacket examplePacket = (ExamplePacket) packet;

                    System.out.println("[Server] Received Packet " + examplePacket.getName() + ":" + examplePacket.getAge() + " in " + packet.getTime() + "ms");
                }
            }
        });

        //Starting server
        hytoraServer.createConnection();

        //Connecting client with name "Lobby-1"
        networkClient.listener(new ClientListener() {
            @Override
            public void onConnect(InetSocketAddress socketAddress) {

            }

            @Override
            public void onHandshake() {

            }

            @Override
            public void onDisconnect() {

            }

            @Override
            public void onReceive(ComponentSender sender, Object object) {

            }

            @Override
            public void packetIn(Packet packet) {
                System.out.println("[IN] " + packet.getClass().getName());
            }

            @Override
            public void packetOut(Packet packet) {
                System.out.println("[OUT] " + packet.getClass().getName());
            }
        }).login(new NetworkLogin("Lobby-1")).createConnection();


        ExamplePacket packet = new ExamplePacket("julheeg", 16);
        networkClient.sendPacket(packet);

    }
}
