package de.lystx.serverselector.cloud.manager.sign.base;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a Group of CloudSigns
 * with Ids
 *
 * Could look like:
 * Lobby:
 *    1 : Lobby-1
 *    2 : Lobby-2
 *    3 : Lobby-3
 * BedWars:
 *    1 : BedWars-1
 *    2 : BedWars-2
 *    3 : BedWars-3
 *
 * It's simple to understand the logic of this
 * To sort the Signs in the SignSelector in Bukkit
 * the Signs in the {@link SignGroup} are already
 * declared with an ID to iterate through all the signs
 * easily
 */
@Getter
public class SignGroup {

    private final String name;
    private Map<Integer, CloudSign> cloudSignHashMap;

    public SignGroup(String name) {
        this.name = name;
        this.cloudSignHashMap = new HashMap<>();
    }

    /**
     * Sets the CloudSigns for this group
     * @param cloudSignHashMap
     */
    public void setCloudSignHashMap(Map<Integer, CloudSign> cloudSignHashMap) {
        this.cloudSignHashMap = cloudSignHashMap;
    }

}
