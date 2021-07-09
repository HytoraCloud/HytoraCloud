
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.RepliableComponent;
import net.hytora.networking.elements.other.ComponentSender;
import net.hytora.networking.elements.other.HytoraLogin;

import java.util.UUID;
import java.util.function.Consumer;


public class ReplyConnectionTest {


    public static void main(String[] args) {


        //Creating server and client objects
        HytoraServer hytoraServer = new HytoraServer(1401);
        HytoraClient hytoraClient = new HytoraClient("127.0.0.1", 1401);

        //Registering channel listener for "example_channel"
        hytoraServer.registerChannelHandler("example_channel", new Consumer<RepliableComponent>() {
            @Override
            public void accept(RepliableComponent repliableComponent) {

                Component component = repliableComponent.getComponent();
                ComponentSender sender = repliableComponent.getSender();

                repliableComponent.reply(com -> {
                    com.setMessage("Hey, I just replied to your request for '" + component.get("name") + "@" + component.get("uuid") + "' !");
                });
            }
        });

        //Starting server
        hytoraServer.createConnection();

        //Connecting client with name "Lobby-1"
        hytoraClient.login(new HytoraLogin("Lobby-1")).createConnection();

        //Creating example component
        Component component = new Component();
        component.setChannel("example_channel");

        //Filling values in component
        component.append(map -> {
            map.put("uuid", UUID.randomUUID());
            map.put("name", "julheeg");
            map.put("verified", false);
        });

        //Sending component
        Component reply = hytoraClient.sendComponentToReply(component);

        System.out.println("[Client] Received Reply : " + reply.getMessage());
    }
}
