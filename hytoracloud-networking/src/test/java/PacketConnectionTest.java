
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.RepliableComponent;
import net.hytora.networking.elements.other.ComponentSender;
import net.hytora.networking.elements.other.HytoraLogin;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import packets.ExamplePacket;

import java.util.function.Consumer;


public class PacketConnectionTest {


    public static void main(String[] args) {


        //Creating server and client objects
        HytoraServer hytoraServer = new HytoraServer(1401);
        HytoraClient hytoraClient = new HytoraClient("127.0.0.1", 1401);

        //Registering packet handler for incoming packets
        hytoraServer.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(HytoraPacket packet) {
                if (packet instanceof ExamplePacket) {
                    ExamplePacket examplePacket = (ExamplePacket) packet;

                    System.out.println("[Server] Received Packet " + examplePacket.getName() + ":" + examplePacket.getAge() + " in " + packet.getTime() + "ms");
                }
            }
        });

        //Starting server
        hytoraServer.createConnection();

        //Connecting client with name "Lobby-1"
        hytoraClient.login(new HytoraLogin("Lobby-1")).createConnection();


        ExamplePacket packet = new ExamplePacket("julheeg", 16);
        hytoraClient.sendPacket(packet);

    }
}
