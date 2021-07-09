package de.lystx.hytoracloud.driver.service.messenger;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ChannelMessage implements Serializable {

    /**
     * The channel to send it to (e.g. "cloud::player")
     */
    private final String channel;

    /**
     * The document containing all the data
     * (e.g. ban reasons or rank updates or sth)
     */
    private final JsonEntity jsonEntity;

    /**
     * The identifier (header) (e.g. "playerUpdate")
     */
    private final String identifier;

    /**
     * Target Components for extra data to not be stored
     * in the document data
     */
    private final String[] targetComponents;

    public ChannelMessage(String channel, JsonEntity jsonEntity, String identifier, String... targetComponents) {
        this.channel = channel;
        this.jsonEntity = jsonEntity;
        this.identifier = identifier;
        this.targetComponents = targetComponents;
    }

}
