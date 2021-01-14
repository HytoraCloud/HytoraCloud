package de.lystx.cloudapi.bukkit.manager.sign.impl;


import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.manager.sign.SignManager;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInCreateCloudSign;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInDeleteCloudSign;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import lombok.Getter;

@Getter
public class SignCreator {

    private final SignManager signManager;

    public SignCreator(SignManager signManager) {
        this.signManager = signManager;
    }

    public void deleteSign(CloudSign cloudSign) {
        this.signManager.getCloudSigns().remove(cloudSign);
        CloudAPI.getInstance().sendPacket(new PacketPlayInDeleteCloudSign(cloudSign));
    }

    public void createSign(CloudSign sign) {
        this.signManager.getCloudSigns().add(sign);
        CloudAPI.getInstance().sendPacket(new PacketPlayInCreateCloudSign(sign));
    }
}
