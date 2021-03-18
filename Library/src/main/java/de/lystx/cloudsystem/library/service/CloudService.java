package de.lystx.cloudsystem.library.service;


import de.lystx.cloudsystem.library.CloudLibrary;
import lombok.Getter;

@Getter
public abstract class CloudService  {

    private final CloudLibrary cloudLibrary;
    private final String name;
    private final CloudServiceType type;

    /**
     * Initialsing the CloudService
     * @param cloudLibrary
     * @param name
     * @param type
     */
    public CloudService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        this.cloudLibrary = cloudLibrary;
        this.name = name;
        this.type = type;
    }

    public enum CloudServiceType {
        UTIL,
        MANAGING,
        NETWORK,
        CONFIG,
        FETCHER,
        OTHER;
    }
}
