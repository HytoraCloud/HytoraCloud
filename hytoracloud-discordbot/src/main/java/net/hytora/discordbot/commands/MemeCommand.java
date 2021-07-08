package net.hytora.discordbot.commands;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.service.util.other.StringCreator;
import jdk.nashorn.api.scripting.URLReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.hytora.discordbot.manager.command.CommandCategory;
import net.hytora.discordbot.manager.command.CommandHandler;

import java.awt.*;
import java.io.BufferedReader;
import java.net.URL;


public class MemeCommand extends CommandHandler {

    public static JsonEntity CURRENT_SUB_REDDIT = new JsonEntity();

    public MemeCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel) {

        try {
            String subReddit;

            if (args.length == 1) {
                subReddit = args[0];
            } else {
                subReddit = "";
            }

            StringCreator stringCreator = new StringCreator();
            BufferedReader bufferedReader = new BufferedReader(new URLReader(new URL("http://meme-api.herokuapp.com/gimme/" + subReddit)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringCreator.append(line);
            }

            JsonEntity vsonObject = new JsonEntity(stringCreator.toString());
            CURRENT_SUB_REDDIT = vsonObject;
            if (vsonObject.getBoolean("nsfw") && !channel.isNSFW()) {
                channel.sendMessage(
                        new EmbedBuilder()
                        .setTitle("Memes | Failed")
                        .setDescription("This channel is not a NSFW-Channel\nSuch pictures can't be shown here\nToggle NSFW-Option for this channel!")
                        .setImage("https://support.discord.com/hc/article_attachments/360007795191/2_.jpg")
                        .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                        .build()
                ).queue();
                return;
            }
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setImage(vsonObject.getString("url"))
                    .setTitle(vsonObject.getString("title"))
                    .setFooter("Subreddit | " + vsonObject.getString("subreddit"), executor.getUser().getEffectiveAvatarUrl());

            channel.sendMessage(embedBuilder.build()).queue(message -> {
                message.addReaction("⬆️").queue();
                message.addReaction("⬇️").queue();
                message.addReaction("\uD83D\uDD01").queue();
            });
        } catch (Exception e) {
            channel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Memes | Error")
                            .setColor(Color.RED)
                            .setThumbnail("http://www.stochasticgeometry.ie/wp-content/uploads/2010/03/RedditProhibited.png")
                            .setDescription("Couldn't get a meme from Reddit!")
                            .setFooter("Executor | " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
                            .build()
            ).queue();
        }

    }


    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
