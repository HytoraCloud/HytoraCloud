package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.packets.communication.*;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public class CloudPlayer implements Serializable {

    private final String name;
    private final UUID uuid;
    private final String ipAddress;
    private String server;
    private String proxy;

    public CloudPlayer(String name, UUID uuid, String ipAddress, String server, String proxy) {
        this.name = name;
        this.uuid = uuid;
        this.ipAddress = ipAddress;
        this.server = server;
        this.proxy = proxy;
    }

    public void sendMessage(CloudExecutor executor, String message) {
        executor.sendPacket(new PacketCommunicationSendMessage(this.uuid, message));
    }

    public void playSound(CloudExecutor executor, String sound, float v1, float v2) {
        executor.sendPacket(new PacketCommunicationPlaySound(this.name, sound, v1, v2));
    }

    public void sendTitle(CloudExecutor executor, String title, String subtitle) {
        executor.sendPacket(new PacketCommunicationSendTitle(this.name, title, subtitle));
    }

    public void fallback(CloudExecutor executor) {
        executor.sendPacket(new PacketCommunicationFallback(this.name));
    }

    public void sendToServer(CloudExecutor executor, String server) {
        executor.sendPacket(new PacketCommunicationSendToServer(this.name, server));
    }

    public void kick(CloudExecutor executor, String reason) {
        executor.sendPacket(new PacketCommunicationKick(this.name, reason));
    }
}
