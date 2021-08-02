package de.lystx.hytoracloud.driver.commons.events.player.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerObject;
import lombok.Getter;

@Getter
public class DriverEventPlayerNPC extends DriverEventPlayer {

    private static final long serialVersionUID = 6318013271660671240L;

    /**
     * The interacted npc
     */
    private final NPCMeta npcMeta;

    /**
     * If action was rightClicked
     */
    private final boolean rightClick;

    public DriverEventPlayerNPC(ICloudPlayer player, NPCMeta npcMeta, boolean rightClick) {
        super(player);

        this.npcMeta = npcMeta;
        this.rightClick = rightClick;
    }

}
