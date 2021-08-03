package de.lystx.hytoracloud.driver.service.fallback;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class Fallback implements Serializable {

    private static final long serialVersionUID = -1224400681672979949L;

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
