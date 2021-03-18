package de.lystx.cloudsystem.receiver.handler;

import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverFiles;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.library.service.util.Serializer;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;

@AllArgsConstructor
public class ReceiverPacketHandlerFiles {

    private final Receiver receiver;

    @PacketHandler
    public void hande(PacketReceiverFiles packet) {
        try {

            FileService service = receiver.getService(FileService.class);
            File bungeeJar = new File(service.getVersionsDirectory(), "bungee.jar");
            File spigotJar = new File(service.getVersionsDirectory(), "spigot.jar");
            File templates = new File(service.getGlobalDirectory(), "templates.zip");

            Serializer<File> serializer = new Serializer<>();

            FileUtils.copyFile(serializer.deserialize(packet.getBungee()), bungeeJar);
            FileUtils.copyFile(serializer.deserialize(packet.getBungee()), spigotJar);
            FileUtils.copyFile(serializer.deserialize(packet.getTemplates()), templates);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
