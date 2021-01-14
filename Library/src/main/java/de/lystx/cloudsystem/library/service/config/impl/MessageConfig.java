package de.lystx.cloudsystem.library.service.config.impl;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class MessageConfig implements Serializable {

    private final String prefix;
    private final String serverStartMessage;
    private final String serverStopMessage;
    private final String alreadyHubMessage;
    private final String noHubMessage;
    private final String maintenanceKickMessage;
    private final String networkStillBootingMessage;
    private final String groupMaintenanceMessage;
    private final String alreadyConnectedMessage;
    private final String serverShutdownMessage;
    private final String errorMessage;

    public MessageConfig(String prefix, String serverStartMessage, String serverStopMessage, String alreadyHubMessage, String noHubMessage, String maintenanceKickMessage, String networkStillBootingMessage, String groupMaintenanceMessage, String alreadyConnectedMessage, String serverShutdownMessage, String errorMessage) {
        this.prefix = prefix;
        this.serverStartMessage = serverStartMessage;
        this.serverStopMessage = serverStopMessage;
        this.alreadyHubMessage = alreadyHubMessage;
        this.noHubMessage = noHubMessage;
        this.maintenanceKickMessage = maintenanceKickMessage;
        this.networkStillBootingMessage = networkStillBootingMessage;
        this.groupMaintenanceMessage = groupMaintenanceMessage;
        this.alreadyConnectedMessage = alreadyConnectedMessage;
        this.serverShutdownMessage = serverShutdownMessage;
        this.errorMessage = errorMessage;
    }
}
