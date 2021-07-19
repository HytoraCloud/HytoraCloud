package utillity;


import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.commons.implementations.ServiceGroupObject;
import de.lystx.hytoracloud.driver.commons.implementations.ServiceObject;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.service.Template;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class PlaceHolderTest {


    public static void main(String[] args) {

        IServiceGroup serviceGroup = new ServiceGroupObject(UUID.randomUUID(), "Proxy", new Template("Proxy", ""), ServiceType.PROXY, "InternalReceiver", -1, 1, 512, 50, 100, false, false, true, new PropertyObject());
        IService testService = new ServiceObject(serviceGroup, 1, 30000);

        PermissionGroup permissionGroup = new PermissionGroup("Player", 9999, "", "", "", "", new LinkedList<>(), new LinkedList<>(), new HashMap<>());

        System.out.println(PlaceHolder.apply("Service : %service% | Group : %group% | Perms: %permission_group%", testService, serviceGroup, permissionGroup));

    }

}