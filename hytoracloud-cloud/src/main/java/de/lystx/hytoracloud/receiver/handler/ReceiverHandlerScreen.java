package de.lystx.hytoracloud.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.service.screen.IScreenManager;

import java.util.LinkedList;
import java.util.function.Consumer;

public class ReceiverHandlerScreen implements IPacketHandler {


    public ReceiverHandlerScreen() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                if (driverRequest.equalsIgnoreCase("SCREEN_GET_LINES")) {

                    String screen = driverRequest.getDocument().getString("screen");

                    IScreenManager instance = CloudDriver.getInstance().getScreenManager();
                    IScreen iScreen = instance.getOrRequest(screen);

                    if (iScreen == null) {
                        return;
                    }
                    driverRequest.createResponse().data(iScreen.getCachedLines() == null ? new LinkedList<>() : iScreen.getCachedLines()).send();
                }
            }
        });
    }

    @Override
    public void handle(IPacket packet) {
    }
}
