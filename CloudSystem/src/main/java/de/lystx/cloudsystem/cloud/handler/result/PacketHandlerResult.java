package de.lystx.cloudsystem.cloud.handler.result;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.result.Result;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerResult extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof ResultPacket) {
            ResultPacket resultPacket = (ResultPacket)packet;
            resultPacket.setResult(new Result(resultPacket.getUniqueId(), resultPacket.read(cloudSystem)));
            cloudSystem.sendPacket(resultPacket);
        }
    }
}