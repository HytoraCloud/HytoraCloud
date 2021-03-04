package de.lystx.cloudsystem.receiver.temp.template.handler;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketTransferFile;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.receiver.Receiver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PacketHandlerTemplate {


    @PacketHandler
    public void handle(PacketTransferFile packet) {
        if (packet.getKey().equalsIgnoreCase("template_transfer")) {
            File dir = new File(packet.getFile().toString());
            dir.mkdirs();
            for (File file : packet.getFile().listFiles()) {
                System.out.println(file.getName());
                try {
                    FileUtils.copyFile(file, new File(dir, file.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String group = packet.getFile().getName();
            Receiver.getInstance().getTemplateTemp().setTemplate(Receiver.getInstance().getGroup(group), packet.getFile());
        }
    }
}
