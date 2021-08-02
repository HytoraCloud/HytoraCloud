package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter @AllArgsConstructor
public class SignLayout implements Serializable {

    private static final long serialVersionUID = 5141408125579266794L;

    /**
     * The name of this animation
     */
    private final String name;

    /**
     * The lines
     */
    private final String[] lines;

    /**
     * The block behind the sign
     */
    private final String blockName;

    /**
     * The block sub-id
     */
    private final int subId;
}
