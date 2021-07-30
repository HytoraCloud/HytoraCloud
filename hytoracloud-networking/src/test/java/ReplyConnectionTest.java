
import de.lystx.hytoracloud.networking.connection.client.NetworkClient;
import de.lystx.hytoracloud.networking.connection.server.NetworkServer;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.component.RepliableComponent;
import de.lystx.hytoracloud.networking.elements.component.ComponentSender;
import de.lystx.hytoracloud.networking.elements.other.NetworkLogin;

import java.util.UUID;
import java.util.function.Consumer;


public class ReplyConnectionTest {


    public static void main(String[] args) {


        //Creating server and client objects
        NetworkServer hytoraServer = new NetworkServer(1401);
        NetworkClient networkClient = new NetworkClient("127.0.0.1", 1401);

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
        networkClient.login(new NetworkLogin("Lobby-1")).createConnection();

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
        Component reply = networkClient.sendComponentToReply(component);

        System.out.println("[Client] Received Reply : " + reply.getMessage());
    }
}
