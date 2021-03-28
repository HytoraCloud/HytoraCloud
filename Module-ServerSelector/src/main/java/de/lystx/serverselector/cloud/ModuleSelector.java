package de.lystx.serverselector.cloud;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.serverselector.cloud.manager.npc.NPCService;
import de.lystx.serverselector.cloud.handler.PacketHandlerCloudSign;
import de.lystx.serverselector.cloud.handler.PacketHandlerNPC;
import de.lystx.serverselector.cloud.manager.sign.SignService;
import de.lystx.serverselector.packets.PacketOutServerSelector;
import io.vson.elements.object.VsonObject;
import lombok.SneakyThrows;

public class ModuleSelector extends Module {

    @Override
    public void onLoadConfig(CloudLibrary cloudLibrary) {
        onReload(cloudLibrary);
    }

    @Override
    public void onEnable(CloudLibrary cloudLibrary) {

        cloudLibrary.cloudServices.add(new SignService(cloudLibrary, "SignService", CloudService.CloudServiceType.MANAGING));
        cloudLibrary.cloudServices.add(new NPCService(cloudLibrary, "NPCService", CloudService.CloudServiceType.MANAGING));

        cloudLibrary.getService(CloudNetworkService.class).registerHandler(new PacketHandlerNPC(cloudLibrary));
        cloudLibrary.getService(CloudNetworkService.class).registerHandler(new PacketHandlerCloudSign(cloudLibrary));
    }

    @Override
    public void onDisable(CloudLibrary cloudLibrary) {
        cloudLibrary.getService(SignService.class).save();
        cloudLibrary.getService(NPCService.class).save();

    }

    @Override @SneakyThrows
    public void onReload(CloudLibrary cloudLibrary) {
        SignService service = cloudLibrary.getService(SignService.class);
        if (service == null) {
            return;
        }
        service.load();
        service.loadSigns();

        NPCService npcService = cloudLibrary.getService(NPCService.class);
        npcService.load();

        cloudLibrary.sendPacket(new PacketOutServerSelector(service.getCloudSigns(), new VsonObject(service.getLayOutFile()), npcService.getNPCConfig(), npcService.getDocument()));
    }
}
