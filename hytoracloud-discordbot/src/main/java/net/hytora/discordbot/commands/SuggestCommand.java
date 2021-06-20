package net.hytora.discordbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.hytora.discordbot.Hytora;
import net.hytora.discordbot.manager.command.CommandCategory;
import net.hytora.discordbot.manager.command.CommandHandler;
import net.hytora.discordbot.manager.suggestion.Suggestion;

import java.awt.*;
import java.util.UUID;

public class SuggestCommand extends CommandHandler {

    public SuggestCommand(String name, String description, CommandCategory category, String... aliases) {
        super(name, description, category, aliases);
    }


    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public void execute(String[] args, Message raw, Member executor, TextChannel channel) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("deny") || args[0].equalsIgnoreCase("accept")) {
                if (!executor.hasPermission(Permission.ADMINISTRATOR)) {
                    System.out.println(false);
                    return;
                }

                UUID uniqueId = UUID.fromString(args[1]);

                Suggestion suggestion = Hytora.getHytora().getSuggestionManager().getPendingSuggestions().stream().filter(s -> s.getUniqueId().equals(uniqueId)).findFirst().orElse(null);

                if (suggestion == null) {
                    System.out.println("Nulled suggestion");
                    return;
                }


                if (args[0].equalsIgnoreCase("deny")) {
                    Hytora.getHytora().getSuggestionManager().result(suggestion, Color.RED);
                } else if (args[0].equalsIgnoreCase("accept")) {
                    Hytora.getHytora().getSuggestionManager().result(suggestion, Color.GREEN);
                }
                raw.delete().queue();
                return;
            }
            if (!channel.getId().equalsIgnoreCase(Hytora.getHytora().getJsonConfig().getJson("suggestions").getString("commands"))) {
                raw.delete().queue();
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < args.length; i++) {

                stringBuilder.append(args[i]);
                if ((i + 1) == args.length) {
                    continue;
                }
                stringBuilder.append(" ");
            }

            Suggestion suggestion = new Suggestion(
                    executor.getUser().getAsTag(),
                    stringBuilder.toString(),
                    executor.getUser().getAvatarUrl(),
                    UUID.randomUUID()
            );

            raw.delete().queue(v -> {
                Hytora.getHytora().getSuggestionManager().createSuggestion(executor.getUser(), suggestion);
            });
        }
    }

    @Override
    public void syntax(String command, TextChannel channel, User executor) {

    }
}
