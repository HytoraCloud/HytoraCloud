package de.lystx.module.commands.packets;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

 @Getter @AllArgsConstructor
public class CommandModulePacket extends Packet implements Serializable {

     private final VsonObject vsonObject;

 }
