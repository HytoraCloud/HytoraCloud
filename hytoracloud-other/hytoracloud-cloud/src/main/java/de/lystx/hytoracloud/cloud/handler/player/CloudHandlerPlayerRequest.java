package de.lystx.hytoracloud.cloud.handler.player;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.required.OfflinePlayer;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
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
                driverRequest.createResponse().data(offlinePlayer.getProperty(document.getString("name"))).send();
            } catch (Exception e) {
                if (driverRequest.getKey().equalsIgnoreCase("PLAYER_GET_PROPERTY_SAFELY")) {
                    offlinePlayer.addProperty(document.getString("name"), new PropertyObject());
                    offlinePlayer.update();
                    driverRequest.createResponse().data(new PropertyObject()).send();
                } else {
                    driverRequest.createResponse().success(false).send();
                }
            }
        } else if (driverRequest.getKey().equalsIgnoreCase("PLAYER_ADD_PROPERTY")) {

            try {
                OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(uniqueId);
                PropertyObject property = document.get("property", PropertyObject.class);
                String name = document.getString("name");

                offlinePlayer.addProperty(name, property);
                offlinePlayer.update();

                driverRequest.createResponse(Boolean.class).data(true).send();
            } catch (Exception e) {
                driverRequest.createResponse(Boolean.class).data(false).success(false).exception(e).send();
            }

        } else if (driverRequest.equalsIgnoreCase("PLAYER_GET_PERMISSIONGROUP")) {

            try {
                PermissionGroup permissionGroup = CloudDriver.getInstance().getPermissionPool().getHighestPermissionGroup(uniqueId);
                driverRequest.createResponse(PermissionGroup.class).data(permissionGroup).send();
            } catch (Exception e) {
                driverRequest.createResponse(PermissionGroup.class).data(null).exception(e).send();
            }

        } else if (driverRequest.getKey().equalsIgnoreCase("PLAYER_ADD_GROUP")) {

            PermissionGroup permissionGroup = CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(document.getString("group"));
            int time = document.getInteger("time");
            PermissionValidity unit = PermissionValidity.valueOf(document.getString("unit"));

            try {
                OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(uniqueId);
                driverRequest.createResponse().data(offlinePlayer.addPermissionGroup(permissionGroup, time, unit).pullValue()).send();
            } catch (Exception e) {
                driverRequest.createResponse().exception(e).send();
            }
        } else if (driverRequest.getKey().equalsIgnoreCase("PLAYER_REMOVE_PERMISSION")) {

            try {
                OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(uniqueId);
                driverRequest.createResponse().data(offlinePlayer.removePermission(document.getString("permission")).pullValue()).send();
            } catch (Exception e) {
                driverRequest.createResponse().exception(e).send();
            }
        } else if (driverRequest.getKey().equalsIgnoreCase("PLAYER_ADD_PERMISSION")) {

            try {
                OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(uniqueId);
                driverRequest.createResponse().data(offlinePlayer.addPermission(document.getString("permission")).pullValue()).send();
            } catch (Exception e) {
                driverRequest.createResponse().exception(e).send();
            }
        } else if (driverRequest.getKey().equalsIgnoreCase("PLAYER_REMOVE_GROUP")) {

            PermissionGroup permissionGroup = CloudDriver.getInstance().getPermissionPool().getPermissionGroupByName(document.getString("group"));

            try {
                OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(uniqueId);
                offlinePlayer.removePermissionGroup(permissionGroup);
                offlinePlayer.update();
                driverRequest.createResponse().data(permissionGroup).send();
            } catch (Exception e) {
                driverRequest.createResponse().exception(e).send();
            }
        }
    }
}
