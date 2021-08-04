package de.lystx.hytoracloud.driver.connection.messenger;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.messenger.IChannelHandler;
import de.lystx.hytoracloud.driver.connection.protocol.netty.messenger.PacketChannelMessage;
import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;
import lombok.Getter;

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
           CloudDriver.getInstance().getConnection().registerChannelHandler(channel, new IChannelHandler() {
               @Override
               public void handle(PacketChannelMessage packet, String json, IChannelMessage message) {
                   consumer.accept(message);
               }
           });
        }, () -> CloudDriver.getInstance().getConnection() != null);
    }

    @Override
    public void unregisterChannel(String channel) {
        this.cache.remove(channel);
        //CloudDriver.getInstance().getConnection().unregisterChannelHandlers(channel);
    }

    @Override
    public void sendChannelMessage(IChannelMessage message, Identifiable receiver) {
        CloudDriver.getInstance().getConnection().sendChannelMessage(message);
    }

}
