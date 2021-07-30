package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class KnockbackConfig implements Serializable {

    private static final long serialVersionUID = -2227963553492347878L;

    /**
     * If the config is enabled
     */
    private final boolean enabled;

    /**
     * The push strength
     */
    private final double strength;

    /**
     * The push distance
     */
    private final double distance;

    /**
     * The permission to bypass knockback
     */
    private final String byPassPermission;
}
