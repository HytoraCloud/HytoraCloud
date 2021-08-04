package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.receiver.IReceiverManager;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import javafx.util.Pair;

import java.util.function.Consumer;

public class CloudHandlerReceiverLogin implements IPacketHandler {


    public CloudHandlerReceiverLogin() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                if (driverRequest.equalsIgnoreCase("RECEIVER_LOGIN_REQUEST")) {
                    JsonObject<?> document = driverRequest.getDocument();

                    IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();

                    IReceiver receiver = document.get("receiver", ReceiverObject.class);

                    if (receiverManager.getReceiver(receiver.getName()) != null) {
                        driverRequest.createResponse().data(new Pair<>("§cThere is already a Receiver with name §e" + receiver.getName() + " §cregistered!", false)).send();

                    } else {
                        if (document.getString("key").equalsIgnoreCase(CloudSystem.getInstance().getKeyAuth().getKey())) {
                            driverRequest.createResponse().data(new Pair<>("§7Logged in on §3Main-CloudInstance as §h'§b" + receiver.getName() + "§h'!", true)).send();
                            receiverManager.registerReceiver(receiver);
                        } else {
                            driverRequest.createResponse().data(new Pair<>("§cThe provided key was wrong or connection refused!", false)).send();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void handle(IPacket packet) {

    }
}
