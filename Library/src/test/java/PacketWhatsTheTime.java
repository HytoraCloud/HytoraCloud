import de.lystx.cloudsystem.library.network.packet.AbstractPacket;
import de.lystx.cloudsystem.library.network.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketWhatsTheTime extends AbstractPacket {

    private String executor;
    private UUID caseUUID;
    private long lastTime;


    @Override
    public void read(PacketBuffer buf) throws IOException {
        executor = buf.readString();
        caseUUID = buf.readUUID();
        lastTime = buf.readLong();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeString(executor);
        buf.writeUUID(caseUUID);
        buf.writeLong(lastTime);
    }

}
