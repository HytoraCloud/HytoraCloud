package de.lystx.discord.elements;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import de.lystx.discord.listener.MessageListneer;
import lombok.Getter;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;

@Getter
public class DiscordBot {

    @Getter
    private static DiscordBot instance;

    private DefaultShardManagerBuilder builder;
    private ShardManager shardManager;
    private JDA api;

    private final String token;
    private final String activity;


    public DiscordBot(String token, String activity) {
        instance = this;
        this.token = token;
        this.activity = activity;
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("net.dv8tion.jda");
        rootLogger.setLevel(Level.OFF);
    }

    public void start() {
        this.builder = DefaultShardManagerBuilder.createLight(this.token, GatewayIntent.GUILD_MEMBERS);

        builder.setActivity(Activity.playing(this.activity));
        builder.setStatus(OnlineStatus.IDLE);
        builder.setUseShutdownNow(true);
        builder.addEventListeners(new MessageListneer());
        try {
            this.shardManager = builder.build();
            this.api = new JDABuilder(AccountType.BOT).setToken(token).build();
        } catch (LoginException e) {

        }
    }

    public void setStatus(OnlineStatus status) {
        this.builder.setStatus(status);
    }

    public void stop() {

        if (this.shardManager != null) {
            this.shardManager.shutdown();
        }
    }

    public void registerEvent(Object o) {
        this.builder.addEventListeners(o);
    }


    public MessageAction sendMessage(String textChannel, Color color, String message) {

        return this.api.getTextChannelById(textChannel).sendMessage(
                        new EmbedBuilder()
                                .setColor(
                                        color
                                )
                                .setDescription(
                                        message
                                )
                                .build());
    }
}
