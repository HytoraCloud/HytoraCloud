package de.lystx.hytoracloud.launcher.cloud.handler.player;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import lombok.AllArgsConstructor;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class CloudHandlerPlayerRequest implements Consumer<DriverRequest<?>> {

    @Override
    public void accept(DriverRequest<?> driverRequest) {

        JsonObject<?> document = driverRequest.getDocument();
        if (!document.has("uniqueId")) {
            return;
        }
        UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
        if (driverRequest.getKey().equalsIgnoreCase("PLAYER_GET_PROPERTY") || driverRequest.getKey().equalsIgnoreCase("PLAYER_GET_PROPERTY_SAFELY")) {

            OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(uniqueId);
            try {
                driverRequest.createResponse(PropertyObject.class).data(offlinePlayer.getProperty(document.getString("name"))).send();
            } catch (Exception e) {
                if (driverRequest.getKey().equalsIgnoreCase("PLAYER_GET_PROPERTY_SAFELY")) {
                    offlinePlayer.addProperty(document.getString("name"), new PropertyObject());
                    offlinePlayer.update();
                    driverRequest.createResponse(PropertyObject.class).data(new PropertyObject()).send();
                } else {
                    driverRequest.createResponse(PropertyObject.class).data(null).send();
                }
            }
        } else if (driverRequest.getKey().equalsIgnoreCase("PLAYER_ADD_PROPERTY")) {

            try {
                OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(uniqueId);
                PropertyObject property = (PropertyObject) document.getObject("property");
                String name = document.getString("name");

                offlinePlayer.addProperty(name, property);
                offlinePlayer.update();
                driverRequest.createResponse(Boolean.class).data(true).send();
            } catch (Exception e) {
                driverRequest.createResponse(Boolean.class).data(true).error(new DriverRequestException("An exception occured", 0x07, e.getClass())).send();
            }
        } else if (driverRequest.equalsIgnoreCase("PLAYER_GET_PERMISSIONGROUP")) {

            try {
                PermissionGroup permissionGroup = CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(uniqueId);
                driverRequest.createResponse(PermissionGroup.class).data(permissionGroup).send();
            } catch (Exception e) {
                driverRequest.createResponse(PermissionGroup.class).data(null).error(new DriverRequestException("An exception occured", 0x08, e.getClass())).send();
            }

        }
    }
}
