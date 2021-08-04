package de.lystx.hytoracloud.driver.service.minecraft;

import de.lystx.hytoracloud.driver.service.minecraft.entity.MinecraftPlayer;
import de.lystx.hytoracloud.driver.service.minecraft.plugin.PluginInfo;
import de.lystx.hytoracloud.driver.service.minecraft.world.MinecraftWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class MinecraftInfo implements Serializable {

    private static final long serialVersionUID = -5379097018739287306L;

    /**
     * The minecraft version
     */
    private final String version;

    /**
     * All outgoing channels
     */
    private final String[] outgoingChannels;

    /**
     * All incoming channels
     */
    private final String[] incomingChannels;

    /**
     * All loaded plugins
     */
    private final List<PluginInfo> plugins;

    /**
     * All worlds
     */
    private final List<MinecraftWorld> worlds;

    /**
     * All online players
     */
    private final List<MinecraftPlayer> players;
}
