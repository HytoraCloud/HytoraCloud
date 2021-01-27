package de.lystx.cloudsystem.handler.result;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.result.Result;
import de.lystx.cloudsystem.library.result.ResultPacket;
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
            cloudSystem.getService(CloudNetworkService.class).sendPacket(resultPacket);
        }
    }
}
