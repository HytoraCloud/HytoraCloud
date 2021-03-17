package de.lystx.cloudsystem.library.elements.packets.result.login;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class ResultPacketLoginRequest extends ResultPacket implements Serializable {

    private final CloudConnection connection;

    @Override
    public VsonObject read(CloudLibrary cloudLibrary) {

        VsonObject vsonObject = new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        return vsonObject;
    }
}
