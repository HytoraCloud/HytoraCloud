
import de.lystx.hytoracloud.networking.connection.NetworkConnection;
import de.lystx.hytoracloud.networking.connection.client.NetworkClient;
import de.lystx.hytoracloud.networking.connection.server.NetworkServer;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.other.NetworkLogin;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;
import packets.ExamplePacket;


public class PacketReplyConnectionTest {


    public static void main(String[] args) {


        //Creating server and client objects
        NetworkServer hytoraServer = new NetworkServer(1401);
        NetworkClient networkClient = new NetworkClient("127.0.0.1", 1401);

        //Registering packet handler for incoming packets
        hytoraServer.registerPacketHandler(new PacketHandler() {
            @Override
            public void handle(Packet packet) {
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
        networkClient.login(new NetworkLogin("Lobby-1")).createConnection();


        ExamplePacket packet = new ExamplePacket("julheeg", 16);

        //Getting a reply component of the packet
        Component component = packet.toReply(networkClient);

        /**
         * If you use {@link Packet#reply(ResponseStatus, String)}
         * you will be able to use {@link Component#reply()} to get
         * the status and the message of the reply you replied with
         * but if you use {@link Packet#reply(Component)} or
         * reply with a component in general you wont be able to
         * use the {@link Component#reply()} and just have to work
         * with the component object you get from {@link Packet#toReply(NetworkConnection)}
         */
        Component.Reply reply = component.reply();

        System.out.println("[Client] " + reply.getStatus() + " - " + reply.getMessage());

    }
}
