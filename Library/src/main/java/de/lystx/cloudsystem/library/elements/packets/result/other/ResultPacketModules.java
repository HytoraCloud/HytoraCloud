package de.lystx.cloudsystem.library.elements.packets.result.other;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.cloudsystem.library.service.module.ModuleInfo;
import de.lystx.cloudsystem.library.service.module.ModuleService;

import java.util.LinkedList;
import java.util.List;

public class ResultPacketModules extends ResultPacket<List<ModuleInfo>> {

    @Override
    public List<ModuleInfo> read(CloudLibrary cloudLibrary) {
        List<ModuleInfo> list = new LinkedList<>();
        for (Module module : cloudLibrary.getService(ModuleService.class).getModules()) {
            list.add(module.getInfo());
        }
        return list;
    }
}
