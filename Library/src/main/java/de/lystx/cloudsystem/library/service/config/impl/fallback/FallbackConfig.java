package de.lystx.cloudsystem.library.service.config.impl.fallback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class FallbackConfig implements Serializable {

    private Fallback defaultFallback;
    private List<Fallback> fallbacks;


    /**
     * Returns Fallback for group
     * @param groupName
     * @return
     */
    public Fallback getFallback(String groupName) {
        return this.fallbacks.stream().filter(fallback -> fallback.getGroupName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
    }
}
