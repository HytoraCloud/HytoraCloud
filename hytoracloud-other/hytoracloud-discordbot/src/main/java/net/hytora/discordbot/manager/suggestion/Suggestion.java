package net.hytora.discordbot.manager.suggestion;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Suggestion {

    private final String user;
    private final String suggestion;
    private final String url;
    private final UUID uniqueId;


    private final List<String> upVotes;
    private final List<String> downVotes;

    private String messageId;


    public Suggestion(String user, String suggestion, String url, UUID uniqueId) {
        this.user = user;
        this.suggestion = suggestion;
        this.url = url;
        this.uniqueId = uniqueId;

        this.upVotes = new ArrayList<>();
        this.downVotes = new ArrayList<>();
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getUser() {
        return user;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getUpVotes() {
        return upVotes;
    }

    public List<String> getDownVotes() {
        return downVotes;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
