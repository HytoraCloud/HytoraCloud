package net.hytora.discordbot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hytora.discordbot.Hytora;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter {


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.getChannelType().equals(ChannelType.TEXT)) {
            return;
        }

        Message message = event.getMessage();
        User user = event.getAuthor();
        TextChannel channel = event.getTextChannel();

        if (event.getMessage().getContentRaw().startsWith(Hytora.getHytora().getCommandManager().getPrefix())) {
            if (!Hytora.getHytora().getCommandManager().execute(true, message.getContentRaw(), channel, user, message)) {

                EmbedBuilder embedBuilder = Hytora.getHytora().getLogManager().embedBuilder(Color.RED, "Managing", user, "This command", "does not exist!");
                channel.sendMessage(embedBuilder.build()).queue(message1 -> message1.delete().queueAfter(2, TimeUnit.SECONDS));
            }
        }

    }
}
