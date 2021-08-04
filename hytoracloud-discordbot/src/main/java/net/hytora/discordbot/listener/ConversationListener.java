package net.hytora.discordbot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hytora.discordbot.Hytora;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ConversationListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        if (event.getAuthor().getId().equalsIgnoreCase(Hytora.getHytora().getDiscord().getSelfUser().getId())) {
            return;
        }

        List<User> pinged = event.getMessage().getMentionedUsers();
        if (pinged.isEmpty()) {
            return;
        }
        if (pinged.get(0).getAsTag().startsWith(Hytora.getHytora().getDiscord().getSelfUser().getAsTag())) {

            String replace = event.getMessage().getContentRaw();

            replace = replace.split(pinged.get(0).getAsMention() + " ")[1];

            List<String> response = Hytora.getHytora().getConversationManager().matches(replace, event);


            String extra = response.get(response.size() - 1);

            response.remove(extra);

            if (!response.isEmpty()) {

                event.getMessage().getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.CYAN)
                                .setThumbnail("https://thumbor.forbes.com/thumbor/711x399/https://blogs-images.forbes.com/neilhowe/files/2019/05/TW_All_the_Lonely_People_WEB.jpg?width=960")
                                .setTitle("HytoraCloud | Lonely")
                                .setDescription(response.get(new Random().nextInt(response.size())))
                                .setFooter("Go get some friends " + event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                .build()
                ).queue();
            } else {


                List<String> responses = new LinkedList<>();

                responses.add("Hmmmm, I'm sorry, but I don't have an answer to that!");
                responses.add("Please try something other!");
                responses.add("I'm new to Discord and didn't understand this phrase :(");
                responses.add("Maybe you made a spelling mistake?");

                EmbedBuilder embedBuilder = Hytora.getHytora().getLogManager().embedBuilder(
                        Color.CYAN,
                        "Lonely",
                        Hytora.getHytora().getDiscord().getSelfUser(),
                        responses.get(new Random().nextInt(responses.size()))
                );
                embedBuilder.setFooter(extra, Hytora.getHytora().getDiscord().getSelfUser().getEffectiveAvatarUrl());

                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
