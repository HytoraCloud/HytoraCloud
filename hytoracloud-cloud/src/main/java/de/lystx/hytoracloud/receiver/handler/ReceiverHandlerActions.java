package de.lystx.hytoracloud.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.packets.receiver.*;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;



import java.lang.management.ManagementFactory;
import java.util.function.Consumer;

public class ReceiverHandlerActions implements IPacketHandler {

    public ReceiverHandlerActions() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                if (driverRequest.equalsIgnoreCase("RECEIVER_MEMORY_USAGE")) {

                    if (driverRequest.getDocument().getString("name").equalsIgnoreCase(IReceiver.current().getName())) {
                        long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
                        driverRequest.createResponse().data(used).send();
                    }
                }
            }
        });
    }

    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketReceiverNeedServices) {
            PacketReceiverNeedServices packetReceiverNeedServices = (PacketReceiverNeedServices)packet;
            IReceiver.current().needsServices(packetReceiverNeedServices.getServiceGroup());

        } else if (packet instanceof PacketReceiverStartService) {
            PacketReceiverStartService packetReceiverStartService = (PacketReceiverStartService)packet;
            IReceiver.current().startService(packetReceiverStartService.getService(), service -> {});

        } else if (packet instanceof PacketReceiverStopService) {
            PacketReceiverStopService stopService = (PacketReceiverStopService)packet;
            IReceiver.current().stopService(stopService.getService(), service -> {});

        } else if (packet instanceof PacketReceiverRegisterService) {
            PacketReceiverRegisterService registerService = (PacketReceiverRegisterService)packet;
            IReceiver.current().registerService(registerService.getService());

        }
    }
}
