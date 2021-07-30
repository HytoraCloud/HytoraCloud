package de.lystx.hytoracloud.launcher.cloud.handler.group;

import de.lystx.hytoracloud.driver.cloudservices.managing.template.ITemplate;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.requests.exception.DriverRequestException;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.wrapped.TemplateObject;
import de.lystx.hytoracloud.driver.CloudDriver;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import java.util.function.Consumer;

public class CloudHandlerGroupUpdate implements PacketHandler {

    public CloudHandlerGroupUpdate() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                GroupService groupService = CloudDriver.getInstance().getInstance(GroupService.class);
                if (driverRequest.equalsIgnoreCase("GROUP_SET_MAINTENANCE")) {
                    try {
                        String name = document.getString("name");
                        boolean maintenance = document.getBoolean("maintenance");
                        IServiceGroup group = groupService.getGroup(name);
                        group.setMaintenance(maintenance);
                        group.update();

                        driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();
                    } catch (Exception e) {
                        driverRequest.createResponse().error(new DriverRequestException("An exception occured", 0x09, e.getClass())).data(ResponseStatus.FAILED).send();
                    }

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_TEMPLATE")) {

                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    TemplateObject template = document.get("template", TemplateObject.class);
                    group.setTemplate(template);

                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_PROPERTIES")) {

                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    group.setProperties(JsonObject.serializable(document.getString("properties")));

                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_PROPERTIES")) {
                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    group.setLobby(document.getBoolean("lobby"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_DYNAMIC")) {
                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    group.setDynamic(document.getBoolean("dynamic"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_MAX_PLAYERS")) {
                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    group.setMaxPlayers(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_MEMORY")) {
                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    group.setMemory(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_PERCENT")) {
                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    group.setNewServerPercent(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_MAX_SERVERS")) {
                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    group.setMaxServer(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_SET_MIN_SERVERS")) {
                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    group.setMinServer(document.getInteger("value"));
                    driverRequest.createResponse().data(ResponseStatus.SUCCESS).send();

                } else if (driverRequest.equalsIgnoreCase("GROUP_CREATE_TEMPLATE")) {
                    String name = document.getString("name");
                    IServiceGroup group = groupService.getGroup(name);
                    ITemplate template = group.createTemplate(document.getString("template")).pullValue();
                    driverRequest.createResponse().data(template).send();
                }
            }
        });
    }

    public void handle(Packet packet) {
        if (packet instanceof PacketInUpdateServiceGroup) {
            PacketInUpdateServiceGroup packetInUpdateServiceGroup = (PacketInUpdateServiceGroup)packet;
            IServiceGroup group = packetInUpdateServiceGroup.getServiceGroup();

            CloudDriver.getInstance().getInstance(GroupService.class).updateGroup(group);
            CloudDriver.getInstance().sendPacket(packet);
        }
    }
}
