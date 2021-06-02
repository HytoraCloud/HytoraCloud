package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketChannelMessage;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.service.messenger.ChannelMessage;
import de.lystx.hytoracloud.driver.service.messenger.ChannelMessageListener;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerChannelMessage implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketChannelMessage) {
            PacketChannelMessage packetChannelMessage = (PacketChannelMessage)packet;
            ChannelMessage channelMessage = packetChannelMessage.getChannelMessage();

            if (channelMessage.getTargetComponents() != null && channelMessage.getTargetComponents().length > 0 && !channelMessage.getTargetComponents()[0].equalsIgnoreCase("only_proxy") && channelMessage.getTargetComponents()[0].equalsIgnoreCase("only_bukkit")) {
                if (channelMessage.getTargetComponents()[0].equals("only_proxy") && CloudDriver.getInstance().getThisService().getServiceGroup().getServiceType() == ServiceType.PROXY) {
                    for (ChannelMessageListener channelMessageListener : CloudDriver.getInstance().getChannelMessenger().getListenersOfChannel(channelMessage.getChannel())) {
                        channelMessageListener.onReceiveRaw(channelMessage);
                    }
                }
            } else {
                for (ChannelMessageListener channelMessageListener : CloudDriver.getInstance().getChannelMessenger().getListenersOfChannel(channelMessage.getChannel())) {
                    channelMessageListener.onReceiveRaw(channelMessage);
                }
            }
        }
    }
}

