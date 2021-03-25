package de.lystx.cloudsystem.library.service;


import de.lystx.cloudsystem.library.CloudLibrary;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class CloudService  {

    private CloudLibrary cloudLibrary;
    private String name;
    private CloudServiceType type;

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

    /**
     * Enum to declare which area of utilitty
     * this CloudService belongs to
     * Names are self-explaining
     */
    public enum CloudServiceType {
        UTIL,
        MANAGING,
        NETWORK,
        CONFIG,
        FETCHER,
        OTHER;
    }
}
