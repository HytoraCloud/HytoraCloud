package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.packets.communication.*;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public class CloudPlayer implements Serializable, CloudCommandSender {

    private final String name;
    private final UUID uniqueId;
    private final String ipAddress;
    private String server;
    private String proxy;
    private CloudPlayerData cloudPlayerData;

    public CloudPlayer(String name, UUID uniqueId, String ipAddress, String server, String proxy) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.ipAddress = ipAddress;
        this.server = server;
        this.proxy = proxy;
    }

    @Override
    public void update() {
        PlayerInstance.EXECUTOR.sendPacket(new PacketCommunicationUpdateCloudPlayer(this.name, this));
    }

    public PermissionGroup getPermissionGroup() {
        return PlayerInstance.PERMISSION_POOL.getHighestPermissionGroup(this.name);
    }

    public String getServerGroup() {
        return this.server.split("-")[0];
    }

    @Override
    public void sendMessage(String message) {
        PacketCommunicationSendMessage sendMessage = new PacketCommunicationSendMessage(this.uniqueId, message);
        PlayerInstance.EXECUTOR.sendPacket(sendMessage);
    }

    @Override
    public void sendComponent(CloudComponent cloudComponent) {
        PacketCommunicationSendComponent sendMessage = new PacketCommunicationSendComponent(this.uniqueId, cloudComponent);
        PlayerInstance.EXECUTOR.sendPacket(sendMessage);
    }

    public void playSound(String sound, float v1, float v2) {
        PacketCommunicationPlaySound playSound = new PacketCommunicationPlaySound(this.name, sound, v1, v2);
        PlayerInstance.EXECUTOR.sendPacket(playSound);
    }

    public void sendTitle( String title, String subtitle) {
        PacketCommunicationSendTitle sendTitle = new PacketCommunicationSendTitle(this.name, title, subtitle);
        PlayerInstance.EXECUTOR.sendPacket(sendTitle);
    }

    @Override
    public void fallback() {
        PacketCommunicationFallback fallback = new PacketCommunicationFallback(this.name);
        PlayerInstance.EXECUTOR.sendPacket(fallback);
    }

    @Override
    public void connect(String server) {
        PacketCommunicationSendToServer sendToServer = new PacketCommunicationSendToServer(this.name, server);
        PlayerInstance.EXECUTOR.sendPacket(sendToServer);
    }

    @Override
    public void kick(String reason) {
        PacketCommunicationKick kick = new PacketCommunicationKick(this.name, reason);
        PlayerInstance.EXECUTOR.sendPacket(kick);
    }

    @Override
    public boolean hasPermission(String permission) {
        return PlayerInstance.PERMISSION_POOL.hasPermission(this.name, permission);
    }


    @Override
    public void sendMessage(String prefix, String message) {
        this.sendMessage("§8[§b" + prefix + "§8] §7" + message);
    }
}
