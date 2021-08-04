package de.lystx.hytoracloud.driver.module.def;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.module.IModule;
import de.lystx.hytoracloud.driver.module.IModuleManager;
import de.lystx.hytoracloud.driver.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.module.cloud.ModuleService;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;

import java.util.LinkedList;
import java.util.List;

public class DefaultModuleManager implements IModuleManager {

    @Override
    public List<IModule> getModules() {
        List<IModule> list = new LinkedList<>();
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            for (DriverModule driverModule : CloudDriver.getInstance().getServiceRegistry().getInstance(ModuleService.class).getDriverModules()) {
                list.add(driverModule.getBase());
            }
        } else if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            DriverRequest<List> request = DriverRequest.create("CLOUD_GET_MODULES", "CLOUD", List.class);

            return (List<IModule>) request.execute().setTimeOut(30, new LinkedList<>()).pullValue();
        }
        return list;
    }

    @Override
    public IModule getModule(String name) {
        return this.getModules().stream().filter(iModule -> iModule.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
