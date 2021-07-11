package net.hytora.discordbot.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.hytora.discordbot.Hytora;
import net.hytora.discordbot.util.button.DiscordButton;
import net.hytora.discordbot.util.button.DiscordButtonAction;
import org.jetbrains.annotations.NotNull;

public class DiscordButtonListener extends ListenerAdapter {


    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        Button button = event.getButton();
        TextChannel textChannel = event.getTextChannel();
        Message message = event.getMessage();

        if (button != null && message != null) {

            DiscordButton discordButton = Hytora.getHytora().getDiscordButtons().stream().filter(db -> String.valueOf(db.getId()).equalsIgnoreCase(button.getId())).findFirst().orElse(null);

            if (discordButton == null) {
                return;
            }

            discordButton.getActionConsumer().accept(new DiscordButtonAction(event.getUser(), button, message, textChannel));

        }
    }
}
