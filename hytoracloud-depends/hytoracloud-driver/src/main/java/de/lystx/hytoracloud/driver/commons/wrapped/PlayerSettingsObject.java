package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.featured.IPlayerSettings;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@Getter @AllArgsConstructor
public class PlayerSettingsObject extends WrappedObject<IPlayerSettings, PlayerSettingsObject> implements IPlayerSettings {

    private final Locale locale;
    private final boolean chatColors;
    private final byte renderDistance;
    private final boolean hat, jacket, rightSleeve, leftSleeve, rightPants, leftPants, cape;

    private final IPlayerSettings.ChatMode chatMode;
    private final IPlayerSettings.MainHand mainHand;

    @Override
    public boolean hasCape() {
        return cape;
    }

    @Override
    public boolean hasJacket() {
        return jacket;
    }

    @Override
    public boolean hasLeftSleeve() {
        return leftSleeve;
    }

    @Override
    public boolean hasRightSleeve() {
        return rightSleeve;
    }

    @Override
    public boolean hasLeftPants() {
        return leftPants;
    }

    @Override
    public boolean hasRightPants() {
        return rightPants;
    }

    @Override
    public boolean hasHat() {
        return hat;
    }

    @Override
    public boolean hasChatColors() {
        return chatColors;
    }

    @Override
    public MainHand getMainHand() {
        return mainHand;
    }

    @Override
    public ChatMode getChatMode() {
        return chatMode;
    }

    @Override
    public String toString() {
        return JsonDocument.GSON.toJson(this);
    }

    @Override
    Class<PlayerSettingsObject> getWrapperClass() {
        return PlayerSettingsObject.class;
    }

    @Override
    Class<IPlayerSettings> getInterface() {
        return IPlayerSettings.class;
    }
}
