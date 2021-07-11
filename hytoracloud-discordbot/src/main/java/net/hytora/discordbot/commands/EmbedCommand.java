package net.hytora.discordbot.commands;

import de.lystx.hytoracloud.driver.utils.utillity.StringCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.hytora.discordbot.Hytora;
import net.hytora.discordbot.manager.command.CommandCategory;
import net.hytora.discordbot.manager.command.CommandHandler;

import java.awt.*;

public class EmbedCommand extends CommandHandler {

    public EmbedCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel) {

        if (args.length >= 2) {

            String title = args[0];
            String c = args[1];

            try {
                Color color = (Color) Color.class.getDeclaredField(c).get(Color.WHITE);

                StringCreator stringCreator = new StringCreator();

                for (int i = 2; i < args.length; i++) {
                    stringCreator.singleAppend(args[i]).singleAppend(" ");
                }

                EmbedBuilder embedBuilder = Hytora.getHytora().getLogManager().embedBuilder(color, title, executor.getUser(), stringCreator.toString());
                channel.sendMessage(embedBuilder.build()).queue();

            } catch (IllegalAccessException | NoSuchFieldException e) {
                EmbedBuilder embedBuilder = Hytora.getHytora().getLogManager().embedBuilder(Color.RED, "Failed", executor.getUser(), "There is no such color as '" + c + "' !");
                channel.sendMessage(embedBuilder.build()).queue();

            }
        } else {
            syntax("embed", channel, executor.getUser());
        }
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {
        Hytora.getHytora().getLogManager().preset(
                channel,
                "Embed-Help",
                executor,
                "Use " + getPrefix() + command + " <title> <color> <message> !"
        );
    }
}
