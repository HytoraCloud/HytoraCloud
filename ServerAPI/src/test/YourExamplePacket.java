import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.standalone.manager.CloudNetwork;
import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.elements.chat.CloudComponentAction;
import de.lystx.cloudsystem.library.elements.service.ServiceInfo;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.List;

public class YourExamplePacket {



    public void exampleCloudPlayerUsage() {


        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName("Admin");
        permissionGroup.getEntries().append("teamSpeakID", -1);
        CloudAPI.getInstance().getPermissionPool().updatePermissionGroup(permissionGroup);
    }
}
