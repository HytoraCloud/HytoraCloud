package de.lystx.cloudsystem.library.service.serverselector.npc;

import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class NPCConfig extends VsonObject implements Serializable {

    private int inventoryRows;
    private String inventoryTitle;
    private boolean corners;
    private String connectingMessage;

    private String itemName;
    private List<String> lore;
    private String itemType;

    private List<VsonObject> items;

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
