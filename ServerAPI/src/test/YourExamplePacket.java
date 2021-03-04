import de.lystx.cloudapi.CloudAPI;

import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;

public class YourExamplePacket {



    public void exampleCloudPlayerUsage() {


        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getPermissionGroupFromName("Admin");
        permissionGroup.getEntries().append("teamSpeakID", -1);
        CloudAPI.getInstance().getPermissionPool().updatePermissionGroup(permissionGroup);
    }
}
