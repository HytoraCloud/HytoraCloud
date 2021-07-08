package de.lystx.hytoracloud.bridge.velocity.elements;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PlayerPermissionProvider implements PermissionProvider, PermissionFunction {

    private final Player player;

    @Override
    public Tristate getPermissionValue(String permission) {

        boolean b = CloudDriver.getInstance().getPermissionPool().hasPermission(this.player.getUniqueId(), permission);

        return Tristate.fromBoolean(b);
    }

    @Override
    public PermissionFunction createFunction(PermissionSubject subject) {
        Preconditions.checkState(subject == this.player, "createFunction called with different argument");
        return this;
    }
}
