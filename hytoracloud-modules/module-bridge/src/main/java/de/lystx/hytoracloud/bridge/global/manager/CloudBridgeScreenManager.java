package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.service.screen.IScreenManager;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class CloudBridgeScreenManager implements IScreenManager {

    @Override
    public List<IScreen> getScreens() {
        DriverRequest<List> request = DriverRequest.create("SCREEN_GET_ALL", "CLOUD", List.class);
        return request.execute().setTimeOut(50, new LinkedList<>()).pullValue();
    }

    @Override
    public boolean isInScreen() {
        return false;
    }

    @Override
    public void quitCurrentScreen() {
    }

    @Override
    public IScreen getScreen() {
        return null;
    }

    @Override
    public IScreen getOrRequest(String name) {
        DriverRequest<IScreen> request = DriverRequest.create("SCREEN_GET_NAME", "CLOUD", IScreen.class);
        request.append("name", name);
        return request.execute().setTimeOut(30, null).pullValue();
    }

    @Override
    public void prepare(IScreen screen) {

    }

    @Override
    public void cache(String screen, String line) {

    }

    @Override
    public void registerScreen(String name, IScreen screen) {

    }
}
