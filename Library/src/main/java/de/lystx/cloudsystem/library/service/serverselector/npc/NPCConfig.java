package de.lystx.cloudsystem.library.service.serverselector.npc;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class NPCConfig implements Serializable {

    private final int inventoryRows;
    private final String inventoryTitle;
    private final boolean corners;
    private final String connectingMessage;

    private final String itemName;
    private final List<String> lore;
    private final String itemType;

    private final List<SerializableDocument> items;

    public NPCConfig(int inventoryRows, String inventoryTitle, boolean corners, String connectingMessage, String itemName, List<String> lore, String itemType, List<SerializableDocument> items) {
        this.inventoryRows = inventoryRows;
        this.inventoryTitle = inventoryTitle;
        this.corners = corners;
        this.connectingMessage = connectingMessage;
        this.itemName = itemName;
        this.lore = lore;
        this.itemType = itemType;
        this.items = items;
    }
}
