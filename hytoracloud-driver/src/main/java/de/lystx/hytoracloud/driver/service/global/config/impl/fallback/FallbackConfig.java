package de.lystx.hytoracloud.driver.service.global.config.impl.fallback;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class FallbackConfig implements Serializable {

    /**
     * The default fallback
     */
    private Fallback defaultFallback;

    /**
     * All the other fallbacks
     */
    private List<Fallback> fallbacks;


    /**
     * Returns Fallback for group
     *
     * @param groupName the name of the group
     * @return fallback or null if not found
     */
    public Fallback getFallback(String groupName) {
        return this.fallbacks.stream().filter(fallback -> fallback.getGroupName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
    }

}
