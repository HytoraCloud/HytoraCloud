import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.standalone.manager.CloudNetwork;
import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.chat.CloudComponentAction;
import de.lystx.cloudsystem.library.elements.service.ServiceInfo;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.List;

public class YourExamplePacket {



    public void exampleCloudPlayerUsage() {

        CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get("Lystx"); //Defines the CloudPlayer
        CloudPlayerData cloudPlayerData = cloudPlayer.getCloudPlayerData(); //Returns the players's data
        CloudConnection connection = cloudPlayer.createConnection(); //Builds up a CloudConnection

        cloudPlayer.sendMessage("§7This is a normal message"); //Sends a normal message
        cloudPlayer.playSound("LEVEL_UP", 1F, 1F); //Plays a sound
        cloudPlayer.sendTitle("TItle", "Subtitle"); //Sends a title
        cloudPlayer.fallback(); //Fallbacks a player
        cloudPlayer.connect("Lobby-1"); //Sends a player to a server
        connection.disconnect("§cYou got kicked"); //Kicks a player

        cloudPlayer.sendComponent(
                new CloudComponent("The player Example sent you a friend request! ") //CReates a component
                        .append(
                                new CloudComponent("§8[§aAccept§8]") //adds a component to the main component
                                        .addEvent(CloudComponentAction.CLICK_EVENT_RUN_COMMAND, "/friend accept Example") //Adds event to run command
                                        .addEvent(CloudComponentAction.HOVER_EVENT_SHOW_TEXT, "§7Click me to accept")) //Adds hover text

                        .append(
                                new CloudComponent("§8[§cDeny§8]")
                                        .addEvent(CloudComponentAction.CLICK_EVENT_RUN_COMMAND, "/friend deny Example")
                                        .addEvent(CloudComponentAction.HOVER_EVENT_SHOW_TEXT, "§7Click me to deny"))
        );
    }
}
