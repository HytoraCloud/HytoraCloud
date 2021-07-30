package de.lystx.hytoracloud.bridge.spigot.bukkit.utils;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.bridge.IBukkit;
import de.lystx.hytoracloud.driver.utils.Reflections;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

@Getter @Setter
public class DefaultBukkit implements IBukkit {

    /**
     * The version
     */
    private String version;

    /**
     * if chat is enabled
     */
    private boolean chatSystem;

    /**
     * if nametags is enabled
     */
    private boolean nametags;

    /**
     *  Minecraft Chat format
     */
    private String chatFormat;


    @Override
    public boolean shouldUseChat() {
        return chatSystem;
    }

    @Override
    public void disableChatSystem() {
        chatSystem = false;
    }

    @Override
    public void enableChatSystem() {
        chatSystem = true;
    }

    @Override
    public boolean shouldUseNameTags() {
        return nametags;
    }

    @Override
    public void enableNameTags() {
        nametags = true;
    }

    @Override
    public void disableNameTags() {
        nametags = false;
    }

    /**
     * Checks for higher Versions
     *
     * @return boolean
     */
    public boolean isNewVersion() {
        if (version == null) {
            return false;
        }
        return !version.startsWith("v1_8");
    }

    public DefaultBukkit() {
    }

}
