package de.lystx.cloudsystem.library.network.packet.impl;

import de.lystx.cloudsystem.library.network.packet.response.ResponseStatus;
import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import lombok.NoArgsConstructor;
import de.lystx.cloudsystem.library.network.packet.PacketBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This packet is for respond to anything. Can be used as response to every packet
 */
@NoArgsConstructor
public class PacketRespond extends AbstractPacket {

    /**
     * The header of the response (like "playerInfo")
     */
    public String header;

    /**
     * The message of the respond (can be a list of playerData or just plain messages)
     */
    public List<String> message;

    /**
     * The status of the response (similar to http)
     */
    public ResponseStatus status;

    public PacketRespond(String header, List<String> message, ResponseStatus status) {
        this.header = header;
        this.message = message;
        this.status = status;
    }

    public PacketRespond(String header, String message, ResponseStatus status) {
        this(header, Collections.singletonList(message), status);
    }

    public PacketRespond(boolean state) {
        this("", new ArrayList<>(), state ? ResponseStatus.SUCESS : ResponseStatus.FAILED);
    }

    public PacketRespond(ResponseStatus status) {
        this("", new ArrayList<>(), status);
    }

    /**
     * Gets the message of the response (either an empty string or the first entry of the messages list)
     *
     * @return The message as string
     */
    public String getMessage() {
        if(message.isEmpty()) return "";
        return message.get(0);
    }

    @Override
    public void read(PacketBuffer buf) throws IOException {
        this.header = buf.readString();
        this.message = buf.readStringList();
        this.status = buf.readEnumValue(ResponseStatus.class);
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeString(header);
        buf.writeStringList(message);
        buf.writeEnumValue(status);
    }

}
