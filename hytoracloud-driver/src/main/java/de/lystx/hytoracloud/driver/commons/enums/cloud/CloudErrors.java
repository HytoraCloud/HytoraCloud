package de.lystx.hytoracloud.driver.commons.enums.cloud;

import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum CloudErrors {

    LOGIN_PROXY("You are already on the network", 583, "ProxyBridge -> playerLogin(PlayerConnection)")
    ;


    /**
     * The message
     */
    private final String message;

    /**
     * The error code
     */
    private final int code;

    /**
     * An additional info
     */
    private final String note;

    @Override
    public String toString() {
        return CloudDriver.getInstance().getPrefix() + "§cError: §e" + message + " §8[§7Code: §b#" + code + "§8]";
    }
}
