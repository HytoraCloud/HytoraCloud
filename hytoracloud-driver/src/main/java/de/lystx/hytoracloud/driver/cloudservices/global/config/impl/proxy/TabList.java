package de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @AllArgsConstructor @Setter
public class TabList implements Serializable {

    private static final long serialVersionUID = -3372478432974039029L;
    /**
     * If the tablist is enabled
     */
    private final boolean enabled;

    /**
     * The header
     */
    private String[] headerLines;

    /**
     * The footer
     */
    private String[] footerLines;

}
