package de.lystx.hytoracloud.driver.elements.packets.request.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.packet.EmptyPacket;


/**
 * This packet is used to request all Modules
 * it will respond with all modules in the Handler
 */
@Getter
@Setter
@AllArgsConstructor
public class PacketRequestModules extends EmptyPacket {

}
