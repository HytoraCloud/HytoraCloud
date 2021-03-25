package de.lystx.cloudsystem.library.service.player.featured.labymod;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

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
