package de.lystx.hytoracloud.driver.service.messenger;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.Getter;

@Getter
public class ChannelMessage implements ThunderObject {

    /**
     * The channel to send it to (e.g. "cloud::player")
     */
    private String channel;

    /**
     * The document containing all the data
     * (e.g. ban reasons or rank updates or sth)
     */
    private JsonEntity jsonEntity;

    /**
     * The identifier (header) (e.g. "playerUpdate")
     */
    private String identifier;

    /**
     * Target Components for extra data to not be stored
     * in the document data
     */
    private String[] targetComponents;

    public ChannelMessage(String channel, JsonEntity jsonEntity, String identifier, String... targetComponents) {
        this.channel = channel;
        this.jsonEntity = jsonEntity;
        this.identifier = identifier;
        this.targetComponents = targetComponents;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(channel); //writes channel
        buf.writeString(jsonEntity.toString()); //writes document
        buf.writeString(identifier); //writes identifier

        //checks if components are null
        if (targetComponents != null) {
            //Available
            buf.writeBoolean(true);
            buf.writeInt(targetComponents.length); //length of the components
            for (String targetComponent : targetComponents) {
                buf.writeString(targetComponent); //writes components
            }
        } else {
            //Not available
            buf.writeBoolean(false);
        }
    }

    @Override
    public void read(PacketBuffer buf) {
        channel = buf.readString(); //the channel name
        jsonEntity = new JsonEntity(buf.readString()); //reads the document
        identifier = buf.readString(); //the identifier

        if (buf.readBoolean()) { //checks if components are null
            int size = buf.readInt(); //length of components
            //creates new array with length
            targetComponents = new String[size];
            for (int i = 0; i < size; i++) {
                //reads componentString and adds it
                targetComponents[i] = buf.readString();
            }
        }
    }

}
