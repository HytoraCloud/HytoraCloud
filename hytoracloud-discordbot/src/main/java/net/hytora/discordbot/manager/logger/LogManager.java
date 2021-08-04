package net.hytora.discordbot.manager.logger;

import de.lystx.hytoracloud.driver.utils.other.StringCreator;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;
import net.hytora.discordbot.Hytora;
import net.hytora.discordbot.util.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class LogManager {

    /**
     * The directory where the logs get saved
     */
    private final File directory;
    private final List<String> logs;

    public LogManager(String directory) {
        this.directory = new File(directory);
        this.logs = new ArrayList<>();
    }

    /**
     * Logs a message without a prefix
     *
     * @param message the message
     */
    public void log(String message) {
        this.log("", message);
    }
    /**
     * Logs a message and sends it to the console aswell
     *
     * @param prefix the prefix
     * @param message the message
     */
    public void log(String prefix, String message) {

        if (!prefix.trim().isEmpty()) {
            prefix = Color.BLACK_BRIGHT + "[" + Color.CYAN + prefix.toUpperCase() + Color.BLACK_BRIGHT + "] " + Color.WHITE;
        }
        message = message + "§f";

        message = message.replace("§1", Color.BLUE.toString());
        message = message.replace("§2", Color.GREEN.toString());
        message = message.replace("§3", Color.CYAN_BRIGHT.toString());
        message = message.replace("§4", Color.RED.toString());
        message = message.replace("§5", Color.MAGENTA.toString());
        message = message.replace("§6", Color.YELLOW.toString());
        message = message.replace("§7", Color.WHITE_BRIGHT.toString());
        message = message.replace("§8", Color.BLACK_BRIGHT.toString());
        message = message.replace("§9", Color.BLUE_BRIGHT.toString());
        message = message.replace("§0", Color.BLACK.toString());

        message = message.replace("§a", Color.GREEN_BRIGHT.toString());
        message = message.replace("§b", Color.CYAN.toString());
        message = message.replace("§c", Color.RED_BRIGHT.toString());
        message = message.replace("§d", Color.MAGENTA_BRIGHT.toString());
        message = message.replace("§e", Color.YELLOW_BRIGHT.toString());
        message = message.replace("§f", Color.RESET.toString());

        String log = prefix + message;
        System.out.println(log);
    }


    /**
     * Sends a default created preset message
     *
     * @param textChannel the channel to send it in
     * @param title the title
     * @param message the message
     * @param requester the requester
     */
    public void preset(TextChannel textChannel, String title, User requester, String... message) {
        this.preset(textChannel, title, requester, message1 -> {}, message);
    }
    /**
     * Sends a default created preset message
     *
     * @param textChannel the channel to send it in
     * @param title the title
     * @param message the message
     * @param requester the requester
     */
    public void preset(TextChannel textChannel, String title, User requester, Consumer<Message> consumer, String... message) {
        textChannel.sendMessage(this.embedBuilder(java.awt.Color.CYAN, title, requester, message).build()).queue(consumer);
    }

    /**
     * Sends a default created preset message
     *
     * @param textChannel the channel to send it in
     * @param title the title
     * @param message the message
     * @param requester the requester
     */
    public void preset(TextChannel textChannel, String title, User requester, Consumer<Message> consumer, Button[] actionRows, String... message) {
        textChannel.sendMessage(this.embedBuilder(java.awt.Color.CYAN, title, requester, message).build()).setActionRow(actionRows).queue(consumer);
    }

    /**
     * Creates an {@link EmbedBuilder} for the {@link LogManager#preset(TextChannel, String, User, String...)} Method
     *
     * @param title the title of the message
     * @param requester the requester
     * @param message the message
     * @return builder
     */
    public EmbedBuilder embedBuilder(java.awt.Color color, String title, User requester, String... message) {
        StringCreator stringCreator = new StringCreator();

        for (String msg : message) {
            stringCreator.append(msg);
        }

       return new EmbedBuilder()
                .setThumbnail(Hytora.getHytora().getGuild().getIconUrl())
                .setTitle("Hytora | " + title)
                .setColor(color)
                .setDescription(stringCreator.create())
                .setFooter("Requested by " + requester.getAsTag(), requester.getEffectiveAvatarUrl());
    }

    /**
     * Saves the current log
     */
    public void save() {

    }
}
