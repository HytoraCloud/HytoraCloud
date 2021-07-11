package de.lystx.hytoracloud.driver.service.managing.player.featured.labymod;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Class is used to get
 * Information from a {@link LabyModPlayer}
 * {@link VoiceChatSettings}.
 * This contains if its enabled, the volume
 * and many more values
 */
@Getter @AllArgsConstructor
public class VoiceChatSettings implements Serializable {

    private final boolean enabled;
    private final int surroundRange;
    private final int surroundVolume;
    private final boolean screamerProtection;
    private final int screamerProtectionLevel;
    private final int screamerMaxVolume;
    private final int microphoneVolume;

}
