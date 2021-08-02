package de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc;

import de.lystx.hytoracloud.driver.commons.wrapped.ItemObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class NPCConfig implements Serializable{

    private static final long serialVersionUID = 6044206058339675497L;
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
    private final ItemObject serverItem;

    /**
     * Other items
     */
    private final List<ItemObject> items;


}
