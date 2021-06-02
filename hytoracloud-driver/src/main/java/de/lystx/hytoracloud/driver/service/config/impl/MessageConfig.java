package de.lystx.hytoracloud.driver.service.config.impl;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class MessageConfig implements Serializable, ThunderObject {

    private String prefix;
    private String serverStartMessage;
    private String serverStopMessage;
    private String alreadyHubMessage;
    private String noHubMessage;
    private String maintenanceKickMessage;
    private String networkStillBootingMessage;
    private String groupMaintenanceMessage;
    private String alreadyConnectedMessage;
    private String alreadyOnNetworkMessage;
    private String serverShutdownMessage;
    private String errorMessage;


    @Override
    public void write(PacketBuffer buf) {
        buf.write(prefix, serverStartMessage, serverStopMessage, alreadyHubMessage, noHubMessage, maintenanceKickMessage, networkStillBootingMessage, groupMaintenanceMessage, alreadyConnectedMessage, alreadyOnNetworkMessage, serverShutdownMessage, errorMessage);
    }

    @Override
    public void read(PacketBuffer buf) {
        prefix = buf.readString();
        serverStartMessage = buf.readString();
        serverStopMessage = buf.readString();
        alreadyHubMessage = buf.readString();
        noHubMessage = buf.readString();
        maintenanceKickMessage = buf.readString();
        networkStillBootingMessage = buf.readString();
        groupMaintenanceMessage = buf.readString();
        alreadyConnectedMessage = buf.readString();
        alreadyOnNetworkMessage = buf.readString();
        serverShutdownMessage = buf.readString();
        errorMessage = buf.readString();
    }
}
