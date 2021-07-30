package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.commons.packets.receiver.*;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import java.lang.management.ManagementFactory;

public class ReceiverHandlerActions implements PacketHandler {

    @Override
    public void handle(Packet packet) {

        if (packet instanceof PacketReceiverMemoryUsage) {

            PacketReceiverMemoryUsage packetReceiverMemoryUsage = (PacketReceiverMemoryUsage)packet;
            if (packetReceiverMemoryUsage.getReceiver().getName().equalsIgnoreCase(IReceiver.current().getName())) {

                long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
                packet.reply(component -> component.put("memory", used));
            }

        } else if (packet instanceof PacketReceiverNeedServices) {
            PacketReceiverNeedServices packetReceiverNeedServices = (PacketReceiverNeedServices)packet;
            IReceiver.current().needsServices(packetReceiverNeedServices.getServiceGroup());

        } else if (packet instanceof PacketReceiverStartService) {
            PacketReceiverStartService packetReceiverStartService = (PacketReceiverStartService)packet;
            IReceiver.current().startService(packetReceiverStartService.getService(), service -> packet.success());

        } else if (packet instanceof PacketReceiverStopService) {
            PacketReceiverStopService stopService = (PacketReceiverStopService)packet;
            IReceiver.current().stopService(stopService.getService(), service -> packet.success());

        } else if (packet instanceof PacketReceiverRegisterService) {
            PacketReceiverRegisterService registerService = (PacketReceiverRegisterService)packet;
            IReceiver.current().registerService(registerService.getService());

        }
    }
}
