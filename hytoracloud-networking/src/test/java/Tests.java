
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.other.HytoraLogin;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class Tests {


    public static void main(String[] args) {


        HytoraServer hytoraServer = new HytoraServer(1401);
        HytoraClient hytoraClient = new HytoraClient("127.0.0.1", 1401);

        hytoraServer.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(HytoraPacket packet) {

            }
        });

        hytoraServer.createConnection();

        hytoraClient.login(new HytoraLogin("Lobby-1")).createConnection();


        ExamplePacket examplePacket = new ExamplePacket("Luca", 16);
        hytoraClient.sendPacket(examplePacket);

    }
}
