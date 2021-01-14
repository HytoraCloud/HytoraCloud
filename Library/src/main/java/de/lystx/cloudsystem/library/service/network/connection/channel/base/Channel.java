package de.lystx.cloudsystem.library.service.network.connection.channel.base;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public class Channel extends ArrayList<Object> {

    private static final long serialVersionUID = 8501296964229015349L;
    private String senderID = "UNSIGNED";
    private String senderGroupName = "UNSIGNED";

    public Channel(NetworkChannel networkChannel, Packet packet) {
        this.add(0, networkChannel.getChannelID());
        this.add(packet);
    }

    public Channel(String id, Object... o) {
        this.add(0, id);
        this.addAll(Arrays.asList(o));
    }

    public String id() {
        if (!(get(0) instanceof String)) {
            throw new IllegalArgumentException("Identifier of Packet is not a String");
        }
        return (String)get(0);
    }

    public void sign(String senderID, String senderGroup) {
        this.senderID = senderID;
        this.senderGroupName = senderGroup;
    }
}
