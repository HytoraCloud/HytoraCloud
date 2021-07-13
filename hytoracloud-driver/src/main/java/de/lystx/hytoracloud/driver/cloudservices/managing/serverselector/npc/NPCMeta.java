package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Getter @AllArgsConstructor
public class NPCMeta implements Serializable {

    /**
     * The uuid of this meta
     */
    private final UUID uniqueId;

    /**
     * The name of the NPC
     */
    private final String name;

    /**
     * The skin of the npc
     */
    private final String skin;

    /**
     * The group of this npc
     */
    private final String group;

    /**
     * The serialized locaiton of this npc
     */
    private final Map<String, Object> location;
}