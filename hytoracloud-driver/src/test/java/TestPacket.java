import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.NettyPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.other.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter @AllArgsConstructor
public class TestPacket extends NettyPacket {

    private String name;

    private int age;

    @Override
    public void read(PacketBuffer buf) throws IOException {
        name = buf.readString();
        age = buf.readInt();
    }

    @Override
    public void write(PacketBuffer buf) throws IOException {
        buf.writeString(name);
        buf.writeInt(age);
    }
}
