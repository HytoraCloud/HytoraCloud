package de.lystx.cloudsystem.library.service.config.impl.labymod;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class LabyModConfig implements Serializable {

    private final boolean enabled;
    private final String serverSwitchMessage;
    private final boolean voiceChat;

}
