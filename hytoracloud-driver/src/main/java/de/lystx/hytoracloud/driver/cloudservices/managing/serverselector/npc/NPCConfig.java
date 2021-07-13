package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudItem;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class NPCConfig implements Serializable, Objectable<NPCConfig> {

    /**
     * The rows of the inventory
     */
    private final int inventoryRows;

    /**
     * The title of the inventory
     */
    private final String inventoryTitle;

    /**
     * If corners should be set
     */
    private final boolean corners;

    /**
     * The message when you click a server
     */
    private final String connectingMessage;

    /**
     * The item every service is displayed in
     */
    private final CloudItem serverItem;

    /**
     * Other items
     */
    private final List<CloudItem> items;


}
