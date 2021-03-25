package de.lystx.cloudsystem.library.service.server.other.manager;

import lombok.Getter;

import java.util.*;

/**
 * This Service searches for a free ID
 * for a certain group
 * Example :
 *    Group: Lobby
 *    ID : 1
 *    ----> Lobby-1
 */
@Getter
public class IDService {

    private final Map<String, List<String>> serverIdList;

    public IDService() {
        this.serverIdList = new HashMap<>();
    }

    /**
     * Returns free ID
     * @param group
     * @return
     */
    public int getFreeID(String group) {
        if (!this.serverIdList.containsKey(group)) {
            List<String> ids = new ArrayList<>();
            ids.add(String.valueOf(1));
            this.serverIdList.put(group, ids);
            return 1;
        } else {
            for (int i = 1; i < 20000;) {
                if (this.serverIdList.get(group).contains(String.valueOf(i))) {
                    i++;
                    continue;
                }
                (this.serverIdList.get(group)).add(String.valueOf(i));
                return i;
            }
            return 404;
        }
    }

    /**
     * Marks ID as unused
     * @param group
     * @param id
     */
    public void removeID(String group, int id) {
        List<String> ids = this.serverIdList.getOrDefault(group, new LinkedList<>());
        ids.remove(String.valueOf(id));
        this.serverIdList.put(group, ids);
    }

}
