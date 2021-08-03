package de.lystx.hytoracloud.cloud.handler.group;

import de.lystx.hytoracloud.driver.service.group.IGroupManager;
import de.lystx.hytoracloud.driver.service.template.ITemplate;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;

import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.wrapped.TemplateObject;
import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.response.ResponseStatus;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

import java.util.UUID;
import java.util.function.Consumer;

public class CloudHandlerGroupUpdate implements PacketHandler {

    public CloudHandlerGroupUpdate() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                IGroupManager cloudSideGroupManager = CloudDriver.getInstance().getGroupManager();
                if (driverRequest.equalsIgnoreCase("GROUP_SET_MAINTENANCE")) {
                    try {
                        String name = document.getString("name");
                        boolean maintenance = document.getBoolean("maintenance");
                        IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                        group.setMaintenance(maintenance);

                        driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();
                    } catch (Exception e) {
                        driverRequest.createResponse().exception(e).data(ResponseStatus.FAILED).send();
                    }

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_TEMPLATE")) {

                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    TemplateObject template = document.get("template", TemplateObject.class);
                    group.setTemplate(template);

                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_PROPERTIES")) {

                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    group.setProperties(JsonObject.serializable(document.getString("properties")));

                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_PROPERTIES")) {
                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    group.setLobby(document.getBoolean("lobby"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_DYNAMIC")) {
                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    group.setDynamic(document.getBoolean("dynamic"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_MAX_PLAYERS")) {
                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    group.setMaxPlayers(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_MEMORY")) {
                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    group.setMemory(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_PERCENT")) {
                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    group.setNewServerPercent(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_MAX_SERVERS")) {
                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    group.setMaxServer(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_MIN_SERVERS")) {
                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    group.setMinServer(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_CREATE_TEMPLATE")) {
                    String name = document.getString("name");
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(name);
                    ITemplate template = group.createTemplate(document.getString("template")).pullValue();
                    driverRequest.createResponse().data(template).send();
                } else if (driverRequest.equalsIgnoreCase("GROUP_GET_SYNC_NAME")) {
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(document.getString("name"));
                    driverRequest.createResponse().data(group).send();
                } else if (driverRequest.equalsIgnoreCase("GROUP_GET_SYNC_UUID")) {
                    IServiceGroup group = cloudSideGroupManager.getCachedObject(UUID.fromString(document.getString("uniqueId")));
                    driverRequest.createResponse().data(group).send();
                }

            }
        });
    }

    public void handle(Packet packet) {
        if (packet instanceof PacketInUpdateServiceGroup) {
            PacketInUpdateServiceGroup packetInUpdateServiceGroup = (PacketInUpdateServiceGroup)packet;
            IServiceGroup group = packetInUpdateServiceGroup.getServiceGroup();

            CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).update(group);
            CloudDriver.getInstance().sendPacket(packet);
        }
    }
}
