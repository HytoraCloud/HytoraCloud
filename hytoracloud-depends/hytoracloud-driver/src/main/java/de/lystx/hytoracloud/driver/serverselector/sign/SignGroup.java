package de.lystx.hytoracloud.driver.serverselector.sign;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
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

    /**
     * The name of the sign group
     */
    private final String name;

    /**
     * The cloud signs stored in cache
     */
    private Map<Integer, CloudSign> cloudSigns;

    public SignGroup(String name) {
        this.name = name;
        this.cloudSigns = new HashMap<>();
    }

    public SignGroup(String name, List<CloudSign> cloudSigns) {
        this.name = name;
        this.cloudSigns = new HashMap<>();

        HashMap<Integer, CloudSign> map = new HashMap<>();
        int count = 1;
        for (CloudSign cloudSign : cloudSigns) {
            if (cloudSign.getGroup().equalsIgnoreCase(name)) {
                map.put(count, cloudSign);
                count++;
            }
        }
        this.setCloudSigns(map);
    }

    /**
     * Sets the CloudSigns for this group
     *
     * @param cloudSigns the cloudSigns
     */
    public void setCloudSigns(Map<Integer, CloudSign> cloudSigns) {
        this.cloudSigns = cloudSigns;
    }

}
