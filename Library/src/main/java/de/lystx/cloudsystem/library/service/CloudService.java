package de.lystx.cloudsystem.library.service;


import de.lystx.cloudsystem.library.CloudLibrary;
import lombok.Getter;

@Getter
public abstract class CloudService  {

    private final CloudLibrary cloudLibrary;
    private final String name;
    private final Type type;

    public CloudService(CloudLibrary cloudLibrary, String name, Type type) {
        this.cloudLibrary = cloudLibrary;
        this.name = name;
        this.type = type;
    }

    public enum Type {
        UTIL,
        MANAGING,
        NETWORK,
        CONFIG,
        FETCHER,
        OTHER;
    }
}
