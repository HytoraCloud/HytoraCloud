package de.lystx.hytoracloud.module.serverselector.cloud;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.module.Module;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.npc.NPCService;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.SignService;
import de.lystx.hytoracloud.module.serverselector.packets.PacketOutServerSelector;
import de.lystx.hytoracloud.module.serverselector.cloud.handler.PacketHandlerCloudSign;
import de.lystx.hytoracloud.module.serverselector.cloud.handler.PacketHandlerNPC;
import lombok.Getter;
import lombok.SneakyThrows;

public class ModuleSelector extends Module {

    @Getter
    private static ModuleSelector instance;

    @Override
    public void onLoadConfig() {
        onReload();
    }

    @Override
    public void onEnable() {
        instance = this;
        CloudDriver.getInstance().getServiceRegistry().registerService(new SignService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new NPCService());

        CloudDriver.getInstance().getConnection().addPacketHandler(new PacketHandlerNPC(CloudDriver.getInstance()));
        CloudDriver.getInstance().getConnection().addPacketHandler(new PacketHandlerCloudSign(CloudDriver.getInstance()));
    }

    @Override
    public void onDisable() {
        CloudDriver.getInstance().getInstance(SignService.class).save();
        CloudDriver.getInstance().getInstance(NPCService.class).save();
    }

    @Override @SneakyThrows
    public void onReload() {
        SignService service = CloudDriver.getInstance().getInstance(SignService.class);
        if (service == null) {
            return;
        }
        service.load();
        service.loadSigns();

        NPCService npcService = CloudDriver.getInstance().getInstance(NPCService.class);
        npcService.load();

        CloudDriver.getInstance().sendPacket(new PacketOutServerSelector(service.getCloudSigns(), service.getSignLayOut().getDocument(), npcService.getNPCConfig(), npcService.getJsonEntity()));
    }
}
