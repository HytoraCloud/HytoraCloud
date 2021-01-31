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

    public String getGroup() {
        return this.server.split("-")[0];
    }

    public void sendMessage(CloudExecutor executor, String message) {
        PacketCommunicationSendMessage sendMessage = new PacketCommunicationSendMessage(this.uuid, message);
        //sendMessage.setSendBack(false);
        executor.sendPacket(sendMessage);
    }

    public void playSound(CloudExecutor executor, String sound, float v1, float v2) {
        PacketCommunicationPlaySound playSound = new PacketCommunicationPlaySound(this.name, sound, v1, v2);
        //playSound.setSendBack(false);
        executor.sendPacket(playSound);
    }

    public void sendTitle(CloudExecutor executor, String title, String subtitle) {
        PacketCommunicationSendTitle sendTitle = new PacketCommunicationSendTitle(this.name, title, subtitle);
        //sendTitle.setSendBack(false);
        executor.sendPacket(sendTitle);
    }

    public void fallback(CloudExecutor executor) {
        PacketCommunicationFallback fallback = new PacketCommunicationFallback(this.name);
       // fallback.setSendBack(false);
        executor.sendPacket(fallback);
    }

    public void sendToServer(CloudExecutor executor, String server) {
        PacketCommunicationSendToServer sendToServer = new PacketCommunicationSendToServer(this.name, server);
       // sendToServer.setSendBack(false);
        executor.sendPacket(sendToServer);
    }

    public void kick(CloudExecutor executor, String reason) {
        PacketCommunicationKick kick = new PacketCommunicationKick(this.name, reason);
       // kick.setSendBack(false);
        executor.sendPacket(kick);
    }
}
