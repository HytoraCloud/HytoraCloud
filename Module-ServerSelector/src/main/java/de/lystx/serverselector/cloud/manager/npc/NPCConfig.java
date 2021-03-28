package de.lystx.serverselector.cloud.manager.npc;

import io.vson.elements.object.VsonObject;
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
public class NPCConfig extends VsonObject implements Serializable {

    private final int inventoryRows;
    private final String inventoryTitle;
    private final boolean corners;
    private final String connectingMessage;

    private final String itemName;
    private final List<String> lore;
    private final String itemType;

    private final List<VsonObject> items;

    public NPCConfig(int inventoryRows, String inventoryTitle, boolean corners, String connectingMessage, String itemName, List<String> lore, String itemType, List<VsonObject> items) {
        this.inventoryRows = inventoryRows;
        this.inventoryTitle = inventoryTitle;
        this.corners = corners;
        this.connectingMessage = connectingMessage;
        this.items = items;
        this.lore = lore;
        this.itemName = itemName;
        this.itemType = itemType;

        this.append("inventoryRows", inventoryRows);
        this.append("inventoryTitle", inventoryTitle);
        this.append("corners", corners);
        this.append("connectingMessage", connectingMessage);
        this.append("itemName", itemName);
        this.append("lore", lore);
        this.append("itemType", itemType);
        this.append("items", items);
    }

}
