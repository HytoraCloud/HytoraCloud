package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.messenger.IChannelMessage;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerChat;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;

import java.util.function.Consumer;

public class ProxyHandlerMessage implements Consumer<IChannelMessage> {


    @Override
    public void accept(IChannelMessage channelMessage) {

        JsonDocument document = channelMessage.getDocument();
        String key = channelMessage.getKey();

        if (key.equalsIgnoreCase("PLAYER_CHAT_EVENT")) {

            String player = document.getString("player");
            String message = document.getString("message");

            ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player);

            DriverEventPlayerChat playerChat = new DriverEventPlayerChat(cloudPlayer, message);
            CloudDriver.getInstance().callEvent(playerChat);

        }
    }
}
