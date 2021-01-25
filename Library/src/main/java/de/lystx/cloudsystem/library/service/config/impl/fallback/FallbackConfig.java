package de.lystx.cloudsystem.library.service.config.impl.fallback;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
public class FallbackConfig implements Serializable {

    private Fallback defaultFallback;
    private List<Fallback> fallbacks;

    public FallbackConfig(Fallback defaultFallback, List<Fallback> fallbacks) {
        this.defaultFallback = defaultFallback;
        this.fallbacks = fallbacks;
    }

    public Fallback getFallback(String name) {
        for (Fallback fallback : this.fallbacks) {
            if (fallback.getGroupName().equalsIgnoreCase(name)) {
                return fallback;
            }
        }
        return null;
    }
}
