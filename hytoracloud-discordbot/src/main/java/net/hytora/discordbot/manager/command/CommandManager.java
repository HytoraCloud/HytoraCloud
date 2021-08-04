package net.hytora.discordbot.manager.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.hytora.discordbot.Hytora;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CommandManager {

    /**
     * All registered {@link CommandHandler}s
     */
    private final List<CommandHandler> commands;

    /**
     * The commandPrefix
     */
    private String prefix;

    /**
     * If the manager is active
     */
    private boolean active;

    public CommandManager(String prefix) {
        this.prefix = prefix;
        this.active = true;
        this.commands = new LinkedList<>();
    }

    /**
     * Registers a {@link CommandHandler} for the bot
     *
     * @param commandHandler the handler
     */
    public void registerCommand(CommandHandler commandHandler) {
        this.commands.add(commandHandler);
    }

    /**
     * Executes a Command and checks for prefix
     *
     * @param prefix if a prefix should be used
     * @param line the raw line
     * @param channel the channel where its send
     * @param user the user
     * @param message the message
     * @return if command exists
     */
    public boolean execute(boolean prefix, String line, TextChannel channel, User user, Message message) {

        if (prefix) line = line.substring(1);
        String commandText = line.split(" ")[0];
        final CommandHandler cmd = this.getCommand(commandText);
        if (cmd == null) {
            return false;
        }

        String[] split = line.substring(commandText.length()).split(" ");
        List<String> args = new LinkedList<>();
        for (String argument : split) {
            if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" ")) {
                args.add(argument);
            }
        }
        split = args.toArray(new String[0]);

        Member member = Hytora.getHytora().getGuild().getMember(user);
        if (member == null) {
            Hytora.getHytora().getLogManager().log("ERROR", "§cCouldn't get Member for User §e" + user.getAsTag() + "§c!");
            return true;
        }
        if (!cmd.hasPermission(member)) {

            EmbedBuilder embedBuilder = Hytora.getHytora().getLogManager().embedBuilder(Color.RED, "Permissions", user, "You are not permitted", "to perform this command!");
            channel.sendMessage(embedBuilder.build()).queue();
            return true;
        }
        cmd.execute(split, message, member, channel);
        return true;
    }

    /**
     * Gets a list of all {@link CommandHandler} by a {@link CommandCategory}
     *
     * @param category the category
     * @return list of commands
     */
    public List<CommandHandler> getCommands(CommandCategory category) {
        List<CommandHandler> list = new LinkedList<>();
        for (CommandHandler command : commands) {
            if (command.getCategory().equals(category)) {
                list.add(command);
            }
        }
        return list;
    }

    /**
     * Gets a {@link CommandHandler} by name
     *
     * Even aliases are being searched for
     * @param commandName the name of the commnand or alias
     * @return commandHandler
     */
    public CommandHandler getCommand(String commandName) {
        for (CommandHandler commandInfo : this.commands) {
            if (commandInfo.getName().equalsIgnoreCase(commandName) || Arrays.asList(commandInfo.getAliases()).contains(commandName))
                return commandInfo;
        }
        return null;
    }

    public List<CommandHandler> getCommands() {
        return commands;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isActive() {
        return active;
    }
}
