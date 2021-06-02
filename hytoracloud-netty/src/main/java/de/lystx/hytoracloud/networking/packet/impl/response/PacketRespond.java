package de.lystx.hytoracloud.networking.packet.impl.response;

import de.lystx.hytoracloud.networking.provided.objects.NetworkObject;
import de.lystx.hytoracloud.networking.packet.impl.AbstractPacket;
import de.lystx.hytoracloud.networking.packet.PacketBuffer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This packet is for respond to anything. Can be used as response to every packet
 */
@NoArgsConstructor
public class PacketRespond extends AbstractPacket {

    /**
     * The message of the respond (can be a list of playerData or just plain messages)
     */
    @Getter
    private String message;

    /**
     * The status of the response (similar to http)
     */
    @Getter
    private ResponseStatus status;

    @Getter
    private List<NetworkObject> networkObjects;

    public PacketRespond(String message, ResponseStatus status) {
        this(message, status, new ArrayList<>());
    }

    public PacketRespond(String message, ResponseStatus status, List<NetworkObject> networkObjects) {
        this.message = message;
        this.status = status;
        this.networkObjects = networkObjects;
    }

    public PacketRespond(ResponseStatus status) {
        this("{}", status);
    }

    @SneakyThrows
    @Override
    public void read(PacketBuffer buf) throws IOException {
        this.message = buf.readString();
        this.status = buf.readEnum(ResponseStatus.class);

        int size = buf.readInt();
        this.networkObjects = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            NetworkObject networkObject = NetworkObject.empty();
            networkObject.read(buf);
            this.networkObjects.add(networkObject);
        }
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeString(message);
        buf.writeEnum(status);

        buf.writeInt(networkObjects.size());
        for (NetworkObject networkObject : networkObjects) {
            networkObject.write(buf);
        }
    }

}
