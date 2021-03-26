package de.lystx.serverselector.spigot;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.manager.sign.SignManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotSelector extends JavaPlugin {

    private SignManager signManager;

    @Override
    public void onEnable() {
        this.signManager = new SignManager(CloudServer.getInstance());
    }

    @Override
    public void onDisable() {
        try {
            int animationScheduler = this.signManager.getSignUpdater().getAnimationScheduler();
            CloudAPI.getInstance().getScheduler().cancelTask(animationScheduler);
        } catch (NullPointerException e) {
            System.out.println("[CloudAPI] Couldn't cancel task for SignUpdater!");
        }
    }
}
