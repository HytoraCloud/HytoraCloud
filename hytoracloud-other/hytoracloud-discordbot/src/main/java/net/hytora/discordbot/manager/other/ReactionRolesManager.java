package net.hytora.discordbot.manager.other;

import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.utils.StringCreator;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hytora.discordbot.Hytora;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ReactionRolesManager extends ListenerAdapter {

    /**
     * The textchannel
     */
    private final TextChannel textChannel;

    /**
     * All Roles saved with a given emote
     */
    private final Map<String, String> rolesAndEmotes;

    public ReactionRolesManager(String channel, JsonObject<?> jsonDocument) {

        this.rolesAndEmotes = new HashMap<>();
        this.textChannel = Hytora.getHytora().getDiscord().getTextChannelById(channel);

        for (String role : jsonDocument.keysExclude("channel")) {


            Hytora.getHytora().createRole(
                    JsonObject.gson()
                            .append("color", "GRAY")
                            .append("name", role)
                            .append("mentionable", true)
                            .append("showOrder", false)
                    , newRole -> {
                        Hytora.getHytora().getLogManager().log("REACTION", "§7ReactionRoles created §b" + newRole.getName() + "§8!");
                    });

            this.rolesAndEmotes.put(role, jsonDocument.getString(role));
        }
        this.checkForChannel();
        Hytora.getHytora().getDiscord().addEventListener(this);
    }

    /**
     * Checks if message exists
     */
    private void checkForChannel() {

        for (Message message : this.textChannel.getIterableHistory()) {
            message.delete().queue();
        }

        StringCreator strings = new StringCreator();

        strings.singleAppend("React with an emote to receive a role");
        strings.singleAppend("Remove a reaction to remove a role");
        strings.singleAppend("\n");
        strings.singleAppend("**Roles**");

        for (String s : rolesAndEmotes.keySet()) {
            strings.singleAppend(rolesAndEmotes.get(s) + " = " + s);
        }

        Hytora.getHytora().getLogManager().preset(textChannel, "ReactionRoles", Hytora.getHytora().getDiscord().getSelfUser(), new Consumer<Message>() {
            @Override
            public void accept(Message message) {
                for (String s : rolesAndEmotes.keySet()) {
                    message.addReaction(rolesAndEmotes.get(s)).queue();
                }
            }
        }, strings.toArray());
    }


    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        TextChannel channel = event.getChannel();

        if (!channel.getId().equalsIgnoreCase(this.textChannel.getId())) {
            return;
        }

        if (event.getUser().getId().equalsIgnoreCase(Hytora.getHytora().getDiscord().getSelfUser().getId())) {
            return;
        }

        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();

        String emoji = reactionEmote.getEmoji();
        String role = this.rolesAndEmotes.keySet().stream().filter(s -> this.rolesAndEmotes.get(s).equalsIgnoreCase(emoji)).findFirst().orElse(null);

        if (role == null) {
            return;
        }

        event.getReaction().removeReaction(event.getUser()).queue();

        Role getRole = Hytora.getHytora().getGuild().getRolesByName(role, true).get(0);

        List<Role> roles = new ArrayList<>(Hytora.getHytora().getGuild().getSelfMember().getRoles());

        roles.sort(Comparator.comparingInt(Role::getPosition));
        Role botRole = roles.get(roles.size() - 1);

        Role hasRole = event.getMember().getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(getRole.getName())).findFirst().orElse(null);

        if (hasRole == null) {
            if (getRole.getPosition() < botRole.getPosition()) {
                Hytora.getHytora().getGuild().addRoleToMember(event.getMember(), getRole).queue();
            } else {
                channel.sendMessage(new MessageBuilder("Cant give you the " + getRole.getName() + " because the bot does not have all rights!").build()).queue(message -> message.delete().queueAfter(3L, TimeUnit.SECONDS));
            }
        } else {
            if (getRole.getPosition() < botRole.getPosition()) {
                Hytora.getHytora().getGuild().removeRoleFromMember(event.getMember(), getRole).queue();
            } else {
                channel.sendMessage(new MessageBuilder("Cant remove you the " + getRole.getName() + " because the bot does not have all rights!").build()).queue(message -> message.delete().queueAfter(3L, TimeUnit.SECONDS));
            }

        }

    }
}
