package de.lystx.cloudsystem.receiver.handler;

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

}
