package de.lystx.hytoracloud.driver.service.config.impl.labymod;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class LabyModConfig implements Serializable {

    /**
     * If its enabled
     */
    private boolean enabled;

    /**
     * The message when switching a server
     */
    private String serverSwitchMessage;

    /**
     * If voicechat should be enabled
     */
    private boolean voiceChat;

}
