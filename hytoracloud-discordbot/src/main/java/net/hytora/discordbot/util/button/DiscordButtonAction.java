package net.hytora.discordbot.util.button;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

public class DiscordButtonAction {

    private final User user;
    private final Button button;
    private final Message message;
    private final TextChannel textChannel;

    public DiscordButtonAction(User user, Button button, Message message, TextChannel textChannel) {
        this.user = user;
        this.button = button;
        this.message = message;
        this.textChannel = textChannel;
    }

    public User getUser() {
        return user;
    }

    public Button getButton() {
        return button;
    }

    public Message getMessage() {
        return message;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }
}
