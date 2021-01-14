package de.lystx.cloudsystem.library.service.serverselector.sign.base;

import java.util.HashMap;

public class SignGroup {

    private final String name;
    private HashMap<Integer, CloudSign> cloudSignHashMap;

    public SignGroup(String name) {
        this.name = name;
        this.cloudSignHashMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, CloudSign> getCloudSignHashMap() {
        return cloudSignHashMap;
    }

    public void setCloudSignHashMap(HashMap<Integer, CloudSign> cloudSignHashMap) {
        this.cloudSignHashMap = cloudSignHashMap;
    }


}
