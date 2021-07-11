package de.lystx.hytoracloud.module.serverselector.cloud.manager.npc;

import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
/**
 * NPC CONFIG
 * Containing rows,
 * title name,
 * corners,
 * connectingMessage,
 * itemName,
 * lore,
 * itemType,
 * and a List of items
 */
@AllArgsConstructor
public class NPCConfig implements Serializable, Objectable<NPCConfig> {

    private final int inventoryRows;
    private final String inventoryTitle;
    private final boolean corners;
    private final String connectingMessage;

    private final String itemName;
    private final List<String> lore;
    private final String itemType;

    private final List<PropertyObject> items;


}
