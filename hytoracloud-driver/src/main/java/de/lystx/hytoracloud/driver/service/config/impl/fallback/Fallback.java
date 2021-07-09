package de.lystx.hytoracloud.driver.service.config.impl.fallback;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Fallback implements Serializable {

    /**
     * The priority
     */
    private int priority;

    /**
     * The group it belongs to
     */
    private String groupName;

    /**
     * The permission to access it
     */
    private String permission;

}
