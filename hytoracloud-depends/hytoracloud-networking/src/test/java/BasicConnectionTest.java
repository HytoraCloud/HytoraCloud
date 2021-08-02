
import de.lystx.hytoracloud.networking.connection.client.NetworkClient;
import de.lystx.hytoracloud.networking.connection.server.NetworkServer;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.component.RepliableComponent;
import de.lystx.hytoracloud.networking.elements.component.ComponentSender;
import de.lystx.hytoracloud.networking.elements.other.NetworkLogin;

import java.util.function.Consumer;


public class BasicConnectionTest {


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

                System.out.println("[Server] Received component from '" + sender.getName() + "' :");
                System.out.println("[Server] Message : " + component.getMessage());
                System.out.println("[Server] Component in JSON format : ");
                System.out.println(component.toString());
            }
        });

        //Starting server
        hytoraServer.createConnection();

        //Connecting client with name "Lobby-1"
        networkClient.login(new NetworkLogin("Lobby-1")).createConnection();

        //Creating example component
        Component component = new Component();
        component.setChannel("example_channel");
        component.setMessage("Hey this is an example message");

        //Sending component
        networkClient.sendComponent(component);
    }
}
