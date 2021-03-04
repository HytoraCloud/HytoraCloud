package de.lystx.cloudsystem.library.service.serverselector.sign.base;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class SignGroup {

    private final String name;
    private HashMap<Integer, CloudSign> cloudSignHashMap;

    public SignGroup(String name) {
        this.name = name;
        this.cloudSignHashMap = new HashMap<>();
    }

    /**
     * Sets the CloudSigns for this group
     * @param cloudSignHashMap
     */
    public void setCloudSignHashMap(HashMap<Integer, CloudSign> cloudSignHashMap) {
        this.cloudSignHashMap = cloudSignHashMap;
    }

}
