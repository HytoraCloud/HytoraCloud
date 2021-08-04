import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class SamplePacket extends JsonPacket {

    @PacketSerializable
    private String name;

    @PacketSerializable
    private int age;


}
