package net.hytora.discordbot.commands;

import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.hytora.discordbot.Hytora;
import net.hytora.discordbot.manager.command.CommandCategory;
import net.hytora.discordbot.manager.command.CommandHandler;
import net.hytora.discordbot.manager.suggestion.Suggestion;
import net.hytora.discordbot.util.button.DiscordButton;
import net.hytora.discordbot.util.button.DiscordButtonAction;

import java.awt.*;
import java.util.UUID;
import java.util.function.Consumer;

public class StopCommand extends CommandHandler {

    public StopCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }


    @Override
    public boolean hasPermission(Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel) {

        Hytora.getHytora().getLogManager().preset(channel, "Shutdown", Hytora.getHytora().getDiscord().getSelfUser(), message -> {

            Scheduler.getInstance().scheduleDelayedTask(() -> {
                message.delete().queue(unused -> Hytora.getHytora().shutdown());
            }, 20L);

        }, "HytoraCloud DiscordBot", "will be shut down in", "about 1 second", "and delete this message!");

    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
