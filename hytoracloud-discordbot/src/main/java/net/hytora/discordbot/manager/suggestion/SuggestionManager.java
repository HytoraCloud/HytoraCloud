package net.hytora.discordbot.manager.suggestion;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.internal.interactions.ButtonImpl;
import net.hytora.discordbot.Hytora;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SuggestionManager extends ListenerAdapter {

    /**
     * All current suggestions
     */
    private final List<Suggestion> pendingSuggestions;

    /**
     * The channel where its send
     */
    private final String channel;

    /**
     * The file for the {@link JsonEntity}
     */
    private final File file;

    /**
     * The stored {@link Suggestion}s
     */
    private final JsonEntity jsonEntity;

    public SuggestionManager(String channel) {
        this.pendingSuggestions = new LinkedList<>();
        this.channel = channel;

        File directory = new File("suggestions/"); directory.mkdirs();
        this.file = new File(directory, "pending.json");

        try {
            this.file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.jsonEntity = new JsonEntity(this.file);
        this.jsonEntity.save();

        //Load saved suggestions
        this.jsonEntity.forEach(Suggestion.class, this.pendingSuggestions::add);
        Hytora.getHytora().getLogManager().log("SUGGESTIONS", "§7Loaded §b" + this.pendingSuggestions.size() + " §7Pending §3Suggestions§8!");

        Hytora.getHytora().getDiscord().addEventListener(this);
    }


    /**
     * Saves all {@link Suggestion}s
     */
    public void save() {
        this.jsonEntity.clear();
        for (Suggestion pendingSuggestion : this.pendingSuggestions) {
            this.jsonEntity.append(pendingSuggestion.getUniqueId().toString(), pendingSuggestion);
        }
        this.jsonEntity.save(this.file);
    }

    /**
     * Sets the result of a {@link Suggestion}
     * 
     * @param suggestion the suggestion
     */
    public void result(Suggestion suggestion, Color color) {

        TextChannel channel = Hytora.getHytora().getGuild().getTextChannelById(this.channel);

        User userTag = Hytora.getHytora().getDiscord().getUserByTag(suggestion.getUser());

        if (userTag == null) {
            return;
        }

        Suggestion finalSuggestion = suggestion;
        suggestion = pendingSuggestions.stream().filter(s -> s.getUniqueId().equals(finalSuggestion.getUniqueId())).findFirst().orElse(suggestion);

        String messageId = suggestion.getMessageId();

        if (channel == null || channel.getHistory() == null) {
            return;
        }

        Message message = channel.retrieveMessageById(messageId).complete();

        if (message == null) {
            return;
        }

        if (message.getEmbeds().size() != 0) {
            MessageEmbed messageEmbed = message.getEmbeds().get(0);
            if (messageEmbed.getFooter() == null || messageEmbed.getFooter().getText() == null) {
                return;
            }

            UUID uniqueId = UUID.fromString(messageEmbed.getFooter().getText().split("ID : ")[1]);

            if (uniqueId.equals(suggestion.getUniqueId())) {
                EmbedBuilder embedBuilder = embedBuilder(userTag, suggestion);

                embedBuilder.setColor(color);

                embedBuilder.getDescriptionBuilder().append("\n");
                embedBuilder.getDescriptionBuilder().append("**Result**").append("\n");
                embedBuilder.getDescriptionBuilder().append(color == Color.GREEN ? "Accepted" : "Denied");

                message.editMessage(embedBuilder.build()).setActionRows().queue();

                if (color == Color.RED) {
                    this.pendingSuggestions.remove(suggestion);
                    this.save();
                }
            }
        }
    }


    /**
     * Creates a {@link Suggestion}
     *
     * @param suggestion the suggestion
     */
    public void createSuggestion(User user, Suggestion suggestion) {

        TextChannel channel = Hytora.getHytora().getGuild().getTextChannelById(this.channel);

        if (channel == null) {
            Hytora.getHytora().getLogManager().log("ERROR", "§cCouldn't create Suggestion §e" + suggestion.getUniqueId() + " §cbecause Channel was not found!");
            return;
        }

        channel.sendMessage(embedBuilder(user, suggestion).build()).setActionRow(
                new ButtonImpl("vote_up" , "Upvote [" + suggestion.getUpVotes().size() + "]", ButtonStyle.SUCCESS, false, null),
                new ButtonImpl("vote_down", "Downvote [" + suggestion.getDownVotes().size() + "]", ButtonStyle.DANGER, false, null)
        ).queue(message -> {
            suggestion.setMessageId(message.getId());
            this.update(suggestion);
        });
    }

    /**
     * Gets the {@link EmbedBuilder} for a suggestion
     *
     * @param user the requester
     * @param suggestion the suggestion
     * @return builder
     */
    private EmbedBuilder embedBuilder(User user, Suggestion suggestion) {

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.GRAY);
        embedBuilder.setTitle("Hytora | Suggestion");
        StringBuilder stringCreator = new StringBuilder();

        stringCreator.append("**Submitter**").append("\n");
        stringCreator.append(user.getAsMention()).append("\n");
        stringCreator.append("\n");
        stringCreator.append("**Suggestion**").append("\n");
        stringCreator.append(suggestion.getSuggestion()).append("\n");

        embedBuilder.setDescription(stringCreator.toString());
        embedBuilder.setThumbnail(user.getEffectiveAvatarUrl());
        embedBuilder.setFooter("ID : " + suggestion.getUniqueId(), Hytora.getHytora().getDiscord().getSelfUser().getEffectiveAvatarUrl());

        return embedBuilder;
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {

        Button button = event.getButton();
        TextChannel textChannel = event.getTextChannel();
        Message message = event.getMessage();


        if (button != null && message != null) {
            if (textChannel.getId().equalsIgnoreCase(this.channel)) {

                MessageEmbed messageEmbed = message.getEmbeds().get(0);

                if (messageEmbed.getFooter() == null || messageEmbed.getFooter().getText() == null) {
                    return;
                }

                UUID uniqueId = UUID.fromString(messageEmbed.getFooter().getText().split("ID : ")[1]);

                Suggestion suggestion = this.pendingSuggestions.stream().filter(s -> s.getUniqueId().equals(uniqueId)).findFirst().orElse(null);

                if (suggestion == null) {
                    return;
                }

                User userByTag = Hytora.getHytora().getDiscord().getUserByTag(suggestion.getUser());

                if (userByTag == null) {
                    return;
                }

                if (button.getId() != null && button.getId().startsWith("vote_up")) {
                    if (!suggestion.getUpVotes().contains(userByTag.getId())) {
                        suggestion.getUpVotes().add(userByTag.getId());
                    } else {
                        suggestion.getUpVotes().remove(userByTag.getId());
                    }
                } else {
                    if (!suggestion.getDownVotes().contains(userByTag.getId())) {
                        suggestion.getDownVotes().add(userByTag.getId());
                    } else {
                        suggestion.getDownVotes().remove(userByTag.getId());
                    }
                }


                event.deferEdit().queue();
                try {
                    message.editMessage(this.embedBuilder(userByTag, suggestion).build()
                    ).setActionRow(
                            new ButtonImpl("vote_up", "Upvote [" + suggestion.getUpVotes().size() + "]", ButtonStyle.SUCCESS, false, null),
                            new ButtonImpl("vote_down", "Downvote [" + suggestion.getDownVotes().size() + "]", ButtonStyle.DANGER, false, null)
                    ).queue(msg -> {
                        suggestion.setMessageId(msg.getId());
                        this.update(suggestion);
                    });
                } catch (Exception e) {
                    //IGnoring
                }
            }
        }
    }

    /**
     * Updates a suggestion
     *
     * @param suggestion the suggestion
     */
    public void update(Suggestion suggestion) {

        if (!this.pendingSuggestions.contains(suggestion)) {
            this.pendingSuggestions.add(suggestion);
        } else {
            this.pendingSuggestions.set(this.pendingSuggestions.indexOf(suggestion), suggestion);
        }

        this.save();
    }

    public List<Suggestion> getPendingSuggestions() {
        return pendingSuggestions;
    }

    public String getChannel() {
        return channel;
    }

    public File getFile() {
        return file;
    }

    public JsonEntity getJsonBuilder() {
        return jsonEntity;
    }
}
