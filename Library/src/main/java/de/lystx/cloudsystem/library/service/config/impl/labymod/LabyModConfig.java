package de.lystx.cloudsystem.library.service.config.impl.labymod;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class LabyModConfig implements Serializable {

    private final boolean enabled;
    private final String serverSwitchMessage;
    private final boolean voiceChat;

    public LabyModConfig(boolean enabled, String serverSwitchMessage, boolean voiceChat) {
        this.enabled = enabled;
        this.serverSwitchMessage = serverSwitchMessage;
        this.voiceChat = voiceChat;
    }
}
