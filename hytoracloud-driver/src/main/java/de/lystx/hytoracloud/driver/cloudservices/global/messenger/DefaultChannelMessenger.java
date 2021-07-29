package de.lystx.hytoracloud.driver.cloudservices.global.messenger;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import lombok.Getter;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.connection.server.HytoraServer;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.RepliableComponent;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class DefaultChannelMessenger implements IChannelMessenger {

    private final Map<String, Consumer<IChannelMessage>> cache;

    public DefaultChannelMessenger() {
        this.cache = new HashMap<>();


    }

    @Override
    public void registerChannel(String channel, Consumer<IChannelMessage> consumer) {
        this.cache.put(channel, consumer);
        CloudDriver.getInstance().executeIf(() -> {
            CloudDriver.getInstance().getConnection().registerChannelHandler(channel, new Consumer<RepliableComponent>() {
                @Override
                public void accept(RepliableComponent reply) {
                    Component component = reply.getComponent();
                    if (component.has("iMessage")) {
                        IChannelMessage iChannelMessage = component.get("iMessage");
                        consumer.accept(iChannelMessage);
                    }
                }
            });
        }, () -> CloudDriver.getInstance().getConnection() != null);
    }

    @Override
    public void unregisterChannel(String channel) {
        this.cache.remove(channel);
        CloudDriver.getInstance().getConnection().unregisterChannelHandlers(channel);
    }

    @Override
    public void sendChannelMessage(IChannelMessage message, Identifiable receiver) {

        Component component = message.toComponent();
        if (receiver != null) {
            component.setReceiver(receiver.getName());
        } else {
            component.setReceiver("ALL");
        }

        HytoraConnection connection = CloudDriver.getInstance().getConnection();
        if (connection instanceof HytoraClient) {
            HytoraClient client = (HytoraClient)connection;
            component.setReceiver("SERVER");
            client.sendComponent(component);
        } else {
            HytoraServer server = (HytoraServer) connection;
            if (receiver != null) {
                server.sendObject(component, receiver.getName());
            } else {
                server.sendComponent(component);
            }
        }
    }

}
