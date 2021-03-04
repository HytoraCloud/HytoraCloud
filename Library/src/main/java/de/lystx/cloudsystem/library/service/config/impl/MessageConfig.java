package de.lystx.cloudsystem.library.service.config.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
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
    private final String alreadyOnNetworkMessage;
    private final String serverShutdownMessage;
    private final String errorMessage;

}
