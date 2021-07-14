package de.lystx.hytoracloud.driver.cloudservices.global.config.impl.fallback;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Fallback implements Serializable {

    /**
     * The priority
     */
    private final int priority;

    /**
     * The group it belongs to
     */
    private final String groupName;

    /**
     * The permission to access it
     */
    private final String permission;

}
