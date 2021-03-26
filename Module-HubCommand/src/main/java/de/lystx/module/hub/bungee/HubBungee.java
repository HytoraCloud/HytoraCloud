package de.lystx.module.hub.bungee;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.module.hub.bungee.command.HubCommand;
import net.md_5.bungee.api.plugin.Plugin;

public class HubBungee extends Plugin {

    @Override
    public void onEnable() {
        CloudAPI.getInstance().registerCommand(new HubCommand());
    }
}
