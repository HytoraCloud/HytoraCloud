package net.hytora.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.hytora.discordbot.Hytora;
import net.hytora.discordbot.manager.command.CommandCategory;
import net.hytora.discordbot.manager.command.CommandHandler;

import java.awt.*;
import java.util.List;

public class HelpCommand extends CommandHandler {

    public HelpCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel) {

        StringBuilder sb = new StringBuilder();

        for (CommandCategory value : CommandCategory.values()) {
            sb.append("**").append(value.name()).append("**").append("\n");
            final List<CommandHandler> commands = Hytora.getHytora().getCommandManager().getCommands(value);
            if (!commands.isEmpty()) {
                for (CommandHandler command : commands) {
                    sb.append("  » " + command.getName() + " | " + command.getDescription()).append("\n");
                }
            } else {
                sb.append("  » No commands for this category!").append("\n");
            }
        }

        channel.sendMessage(
            new EmbedBuilder()
                .setTitle("HytoraCloud | Help")
                .setDescription(sb.toString())
                .setColor(Color.CYAN)
                .setThumbnail(Hytora.getHytora().getGuild().getIconUrl())
                .setFooter("Requested by " + executor.getUser().getAsTag(), executor.getUser().getEffectiveAvatarUrl())
            .build()
        ).queue();

    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
