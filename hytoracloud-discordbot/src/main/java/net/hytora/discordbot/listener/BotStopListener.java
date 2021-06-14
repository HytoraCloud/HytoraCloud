package net.hytora.discordbot.listener;

import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.hytora.discordbot.Hytora;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class BotStopListener extends ListenerAdapter {


    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        Button button = event.getButton();
        TextChannel textChannel = event.getTextChannel();
        Message message = event.getMessage();

        if (button != null && button.getStyle() == ButtonStyle.DANGER && message != null) {
            if (textChannel.getId().equalsIgnoreCase(Hytora.getHytora().getBotManaging().getId())) {
                EmbedBuilder embedBuilder = Hytora.getHytora().getLogManager().embedBuilder(Color.DARK_GRAY,"Shutdown", event.getUser(), "The HytoraCloud Bot", "Is shutting down in 1 Second...");
                message.editMessage(embedBuilder.build()
                ).queue(message1 -> {
                    Scheduler.getInstance().scheduleDelayedTask(() -> {
                        message1.delete().queue(unused -> Hytora.getHytora().shutdown());
                    }, 20L);
                });
            }
        }
    }
}
