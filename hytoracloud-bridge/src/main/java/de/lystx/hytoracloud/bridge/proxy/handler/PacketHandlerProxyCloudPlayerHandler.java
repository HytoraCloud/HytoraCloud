package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.proxy.CloudProxy;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.chat.CloudComponent;
import de.lystx.hytoracloud.driver.elements.chat.CloudComponentAction;
import de.lystx.hytoracloud.driver.elements.packets.both.player.*;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketConnectServer;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPing;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import io.thunder.packet.impl.response.ResponseStatus;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PacketHandlerProxyCloudPlayerHandler implements PacketHandler {


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketConnectGroup) {
            PacketConnectGroup packetConnectGroup = (PacketConnectGroup) packet;

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packetConnectGroup.getName());
            if (player == null) {
                return;
            }

            List<Service> services = CloudDriver.getInstance().getServiceManager().getServices(CloudDriver.getInstance().getServiceManager().getServiceGroup(packetConnectGroup.getGroup()));
            Service service = services.get(new Random().nextInt(services.size()));

            player.connect(ProxyServer.getInstance().getServerInfo(service.getName()));
        } else if (packet instanceof PacketRequestPing) {

            PacketRequestPing packetOutPingRequest = (PacketRequestPing)packet;
            UUID uuid = packetOutPingRequest.getUuid();
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            if (player == null) {
                return;
            }

            packet.respond(ResponseStatus.SUCCESS, player.getPing());

        } else if (packet instanceof PacketKickPlayer) {

            PacketKickPlayer packetKickPlayer = (PacketKickPlayer)packet;
            ProxyServer.getInstance().getPlayer(packetKickPlayer.getName()).disconnect(packetKickPlayer.getReason());

        } else if (packet instanceof PacketConnectServer) {
            PacketConnectServer packetConnectServer = (PacketConnectServer)packet;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packetConnectServer.getName());
            if (player == null) {
                return;
            }
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(packetConnectServer.getServer());
            if (serverInfo == null) {
                return;
            }
            player.connect(serverInfo);
        } else if (packet instanceof PacketSendComponent) {
            PacketSendComponent packetSendComponent = (PacketSendComponent)packet;
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(((PacketSendComponent)packet).getUuid());
            if (player == null) {
                return;
            }
            CloudComponent cloudComponent = packetSendComponent.getCloudComponent();
            player.sendMessage(this.fromCloud(cloudComponent));
        } else if (packet instanceof PacketFallback) {
            PacketFallback packetFallback = (PacketFallback)packet;
            CloudProxy.getInstance().getHubManager().sendPlayerToFallback(ProxyServer.getInstance().getPlayer(packetFallback.getName()));
        } else if (packet instanceof PacketSendMessage) {
            PacketSendMessage packetSendMessage = (PacketSendMessage)packet;

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(((PacketSendMessage)packet).getUuid());
            if (player == null) {
                return;
            }
            player.sendMessage(packetSendMessage.getMessage());
        }
    }


    /**
     * Creates a {@link TextComponent} from a {@link CloudComponent}
     *
     * @param cloudComponent the cloudComponent
     * @return built md5 textComponent
     */
    public TextComponent fromCloud(CloudComponent cloudComponent) {
        TextComponent textComponent = new TextComponent(cloudComponent.getMessage());
        cloudComponent.getActions().forEach((action1, objects) -> {

            if (action1 != null && objects != null) {
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
