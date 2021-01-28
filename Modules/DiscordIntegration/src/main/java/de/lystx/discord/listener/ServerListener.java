package de.lystx.discord.listener;

import de.lystx.cloudsystem.library.elements.events.other.ServiceStartEvent;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.event.raw.SubscribeEvent;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.discord.DiscordIntegration;
import de.lystx.discord.elements.DiscordBot;

import java.awt.*;

public class ServerListener {

    @SubscribeEvent
    public void onStart(ServiceStartEvent event) {
        Service service = event.getService();
        DiscordIntegration.getInstance().getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
            DiscordBot.getInstance().sendMessage(DiscordIntegration.getInstance().getOtherChannel(), Color.CYAN, "server " + service.getName() + " started!");
        }, 60L);
    }
}
