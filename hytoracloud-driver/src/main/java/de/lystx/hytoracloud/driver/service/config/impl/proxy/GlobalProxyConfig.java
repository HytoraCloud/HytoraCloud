package de.lystx.hytoracloud.driver.service.config.impl.proxy;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor @Getter @Setter
public class GlobalProxyConfig implements ThunderObject {

    private Integer proxyStartPort;
    private Integer serverStartPort;
    private boolean proxyProtocol;
    private boolean maintenance;
    private boolean hubCommand;
    private List<String> whitelistedPlayers;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeInt(proxyStartPort);
        buf.writeInt(serverStartPort);
        buf.writeBoolean(proxyProtocol);
        buf.writeBoolean(maintenance);
        buf.writeBoolean(hubCommand);
        buf.writeInt(whitelistedPlayers.size());
        for (String whitelistedPlayer : whitelistedPlayers) {
            buf.writeString(whitelistedPlayer);
        }
    }

    @Override
    public void read(PacketBuffer buf) {
        proxyStartPort = buf.readInt();
        serverStartPort = buf.readInt();
        proxyProtocol = buf.readBoolean();
        maintenance = buf.readBoolean();
        hubCommand = buf.readBoolean();
        int size = buf.readInt();
        whitelistedPlayers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            whitelistedPlayers.add(buf.readString());
        }
    }
}
