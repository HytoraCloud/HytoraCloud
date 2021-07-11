package de.lystx.hytoracloud.driver.service.global.config.impl;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class MessageConfig implements Serializable {

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


}
