
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.other.HytoraLogin;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import packets.ExamplePacket;


public class PacketReplyConnectionTest {


    public static void main(String[] args) {


        //Creating server and client objects
        HytoraServer hytoraServer = new HytoraServer(1401);
        HytoraClient hytoraClient = new HytoraClient("127.0.0.1", 1401);

        //Registering packet handler for incoming packets
        hytoraServer.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(HytoraPacket packet) {
                if (packet instanceof ExamplePacket) {

                    //Replying to this packet
                    packet.reply(ResponseStatus.SUCCESS, "The packet was received!");

                    /* You could also use this way to reply
                    packet.reply(component -> {
                        component.setChannel("main");
                        component.setMessage("Hey this packet was received");
                    });*/
                }
            }
        });

        //Starting server
        hytoraServer.createConnection();

        //Connecting client with name "Lobby-1"
        hytoraClient.login(new HytoraLogin("Lobby-1")).createConnection();


        ExamplePacket packet = new ExamplePacket("julheeg", 16);

        //Getting a reply component of the packet
        Component component = packet.toReply(hytoraClient);

        /**
         * If you use {@link HytoraPacket#reply(ResponseStatus, String)}
         * you will be able to use {@link Component#reply()} to get
         * the status and the message of the reply you replied with
         * but if you use {@link HytoraPacket#reply(Component)} or
         * reply with a component in general you wont be able to
         * use the {@link Component#reply()} and just have to work
         * with the component object you get from {@link HytoraPacket#toReply(HytoraConnection)}
         */
        Component.Reply reply = component.reply();

        System.out.println("[Client] " + reply.getStatus() + " - " + reply.getMessage());

    }
}
