package de.lystx.discord;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.discord.elements.DiscordBot;
import de.lystx.discord.listener.ServerListener;
import lombok.Getter;

@Getter
public class DiscordIntegration extends Module {

    @Getter
    private static DiscordIntegration instance;

    private DiscordBot discordBot;

    private String token;
    private String activity;
    private String serverChannel;
    private String playerChannel;
    private String otherChannel;

    @Override
    public void onEnable(CloudLibrary cloudLibrary) {
        instance = this;
        this.registerEvent(new ServerListener());
    }

    @Override
    public void onDisable(CloudLibrary cloudLibrary) {
        this.discordBot.stop();
    }

    @Override
    public void onLoadConfig(CloudLibrary cloudLibrary) {
        Document document = this.getConfig();
        this.token = document.getString("token", "yourToken");
        this.activity = document.getString("activity", " on HytoraCloud");
        this.serverChannel = document.getString("serverChannel", "serverChannel");
        this.playerChannel = document.getString("playerChannel", "playerChannel");
        this.otherChannel = document.getString("otherChannel", "otherChannel");
        document.save();

        this.discordBot = new DiscordBot(token, activity);
        this.discordBot.start();
    }
}
