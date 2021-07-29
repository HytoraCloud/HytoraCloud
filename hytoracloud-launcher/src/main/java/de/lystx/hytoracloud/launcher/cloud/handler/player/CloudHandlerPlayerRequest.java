package de.lystx.hytoracloud.launcher.cloud.handler.player;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import lombok.AllArgsConstructor;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public class CloudHandlerPlayerRequest implements Consumer<DriverRequest<?>> {

    @Override
    public void accept(DriverRequest<?> driverRequest) {

        if (driverRequest.getKey().equalsIgnoreCase("PLAYER_GET_PROPERTY") || driverRequest.getKey().equalsIgnoreCase("PLAYER_GET_PROPERTY_SAFELY")) {

            JsonObject<?> document = driverRequest.getDocument();
            OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPlayerManager().getOfflinePlayer(UUID.fromString(document.getString("uniqueId")));
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
        } else if (driverRequest.getKey().equalsIgnoreCase("")) {

        }
    }
}
