package de.lystx.hytoracloud.driver.cloudservices.managing.event.base;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.handling.IListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handling.EventHandler;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerConnectionObject;
import de.lystx.hytoracloud.driver.commons.events.network.DriverEventNetworkPing;
import de.lystx.hytoracloud.driver.commons.events.other.*;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerLogin;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerQuit;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerServerChange;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;

public class DefaultListener implements IListener {

    @EventHandler
    public void handleQueue(DriverEventServiceQueue event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerQueue(service));
    }

    @EventHandler
    public void handleStarted(DriverEventServiceStarted event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerStarted(service));
    }

    @EventHandler
    public void handleRegister(DriverEventServiceRegister event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerRegister(service));
    }

    @EventHandler
    public void handleUpdate(DriverEventServiceUpdate event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerUpdate(service));
    }

    @EventHandler
    public void handleStop(DriverEventServiceStop event) {
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerStop(service));
    }

    @EventHandler
    public void handlePlayerJoin(DriverEventPlayerLogin event) {
        ICloudPlayer player = event.getPlayer();

        NetworkHandler.run(networkHandler -> networkHandler.onPlayerJoin(player));
    }

    @EventHandler
    public void handlePlayerQuit(DriverEventPlayerQuit event) {
        ICloudPlayer player = event.getPlayer();

        NetworkHandler.run(networkHandler -> networkHandler.onPlayerQuit(player));
    }

    @EventHandler
    public void handlePlayerServiceChange(DriverEventPlayerServerChange event) {
        ICloudPlayer player = event.getPlayer();
        IService service = event.getService();

        NetworkHandler.run(networkHandler -> networkHandler.onServerChange(player, service));
    }

    @EventHandler
    public void handleNetworkPing(DriverEventNetworkPing event) {
        PlayerConnectionObject connection = event.getConnection();

        NetworkHandler.run(networkHandler -> networkHandler.onNetworkPing(connection));
    }

}
