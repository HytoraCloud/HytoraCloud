
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.wrapped.PlayerObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter @ToString @AllArgsConstructor
public class TestPacket extends JsonPacket {

    @PacketSerializable(value = PlayerObject.class)
    private List<ICloudPlayer> names;
}
