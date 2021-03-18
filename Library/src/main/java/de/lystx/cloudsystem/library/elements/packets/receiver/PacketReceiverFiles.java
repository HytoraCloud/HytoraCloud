package de.lystx.cloudsystem.library.elements.packets.receiver;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.util.Serializer;
import lombok.Getter;

import java.io.*;

@Getter
public class PacketReceiverFiles extends Packet implements Serializable {

    private final byte[] bungee, spigot, templates;

    public PacketReceiverFiles(File spigotJar, File bungeeCordJar, File templatesZip) {

        this.bungee = new Serializer<>(bungeeCordJar).serialize();
        this.spigot = new Serializer<>(spigotJar).serialize();
        this.templates = new Serializer<>(templatesZip).serialize();
    }


}
