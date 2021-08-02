package net.hytora.discordbot.manager.command;

import net.dv8tion.jda.api.entities.*;
import net.hytora.discordbot.Hytora;

public abstract class CommandHandler {

    private final String name;
    private final String description;
    private final String[] aliases;
    private final CommandCategory category;

    public CommandHandler(String name, String description, CommandCategory category, String... aliases) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.aliases = aliases;
    }

    public abstract boolean hasPermission(Member member);

    public abstract void execute(String[] args, Message raw, Member executor, TextChannel channel);

    public abstract void syntax(String command, TextChannel channel, User executor);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public CommandCategory getCategory() {
        return category;
    }

    protected String getPrefix() {
        return Hytora.getHytora().getCommandManager().getPrefix();
    }
}
