package de.lystx.hytoracloud.driver.service.config.impl.labymod;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class LabyModConfig implements ThunderObject {

    /**
     * If its enabled
     */
    private boolean enabled;

    /**
     * The message when switching a server
     */
    private String serverSwitchMessage;

    /**
     * If voicechat should be enabled
     */
    private boolean voiceChat;

    @Override
    public void write(PacketBuffer buf) {
        buf.writeBoolean(enabled);
        buf.writeString(serverSwitchMessage);
        buf.writeBoolean(voiceChat);
    }

    @Override
    public void read(PacketBuffer buf) {
        enabled = buf.readBoolean();
        serverSwitchMessage = buf.readString();
        voiceChat = buf.readBoolean();
    }
}
