package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.chat.CloudComponentAction;
import de.lystx.cloudsystem.library.elements.packets.both.player.*;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

import java.util.List;
import java.util.Random;

@Getter @AllArgsConstructor
public class PacketHandlerProxyCloudPlayerHandler extends PacketHandlerAdapter {


    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketSendMessage) {
            PacketSendMessage packetPlayOutSendMessage = (PacketSendMessage)packet;
            ProxyServer.getInstance().getPlayer(packetPlayOutSendMessage.getUuid()).sendMessage(packetPlayOutSendMessage.getMessage());
        } else if (packet instanceof PacketFallback) {

            PacketFallback fallback = (PacketFallback) packet;
            CloudProxy.getInstance().getHubManager().sendPlayerToFallback(ProxyServer.getInstance().getPlayer(fallback.getName()));
        } else if (packet instanceof PacketSendComponent) {
            PacketSendComponent packetSendComponent = (PacketSendComponent)packet;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packetSendComponent.getUuid());
            if (player == null) {
                return;
            }
            CloudComponent cloudComponent = packetSendComponent.getCloudComponent();
            player.sendMessage(this.fromCloud(cloudComponent));

        } else if (packet instanceof PacketConnectServer) {
            PacketConnectServer server = (PacketConnectServer) packet;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(server.getName());
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server.getServer());
            if (serverInfo == null) {
                return;
            }
            player.connect(serverInfo);
        } else if (packet instanceof PacketConnectGroup) {
            final PacketConnectGroup group = (PacketConnectGroup) packet;
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(group.getName());

            final List<Service> services = CloudAPI.getInstance().getNetwork().getServices(CloudAPI.getInstance().getNetwork().getServiceGroup(group.getGroup()));
            final Service service = services.get(new Random().nextInt(services.size()));

            player.connect(ProxyServer.getInstance().getServerInfo(service.getName()));
        } else if (packet instanceof PacketKickPlayer) {
            PacketKickPlayer kick = (PacketKickPlayer)packet;
            ProxyServer.getInstance().getPlayer(kick.getName()).disconnect(kick.getReason());
        }
    }

    public TextComponent fromCloud(CloudComponent cloudComponent) {
        TextComponent textComponent = new TextComponent(cloudComponent.getMessage());
        cloudComponent.getActions().forEach((action1, objects) -> {

            if (action1 != null && objects != null) {
                ClickEvent.Action action;
                if (action1.equals(CloudComponentAction.CLICK_EVENT_RUN_COMMAND)) {
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, (String) objects[0]));
                } else if (action1.equals(CloudComponentAction.CLICK_EVENT_OPEN_URL)) {
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, (String) objects[0]));
                } else if (action1.equals(CloudComponentAction.CLICK_EVENT_SUGGEST_COMMAND)) {
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, (String) objects[0]));
                } else if (action1.equals(CloudComponentAction.HOVER_EVENT_SHOW_TEXT)) {
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("" + objects[0])}));
                } else if (action1.equals(CloudComponentAction.HOVER_EVENT_SHOW_ENTITY)) {

                } else {

                }

            }
        });
        cloudComponent.getCloudComponents().forEach(component -> textComponent.addExtra(this.fromCloud(component)));
        return textComponent;
    }
}
