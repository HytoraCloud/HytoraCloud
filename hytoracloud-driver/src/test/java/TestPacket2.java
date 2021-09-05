
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.forwarding.ForwardingPacketJson;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString @AllArgsConstructor
public class TestPacket2 extends ForwardingPacketJson {

    @PacketSerializable
    private String name;

}
