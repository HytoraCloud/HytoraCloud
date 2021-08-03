package de.lystx.hytoracloud.cloud.manager.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.service.screen.IScreenManager;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver.PacketReceiverScreenRequest;
import de.lystx.hytoracloud.driver.wrapped.ScreenObject;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;

import java.util.*;

@Getter

public class CloudSideScreenService implements IScreenManager {

    private final Map<String, IScreen> map;

    @Setter
    private Map<String, List<String>> cachedLines;

    /**
     * The current screen
     */
    private IScreen screen;

    /**
     * If currently in screen
     */
    private boolean inScreen;

    /**
     * Sets current screen
     *
     * @param screen the screen
     */
    public void prepare(IScreen screen) {
        this.screen = screen;
        this.inScreen = true;
    }

    public CloudSideScreenService() {
        this.map = new HashMap<>();
        this.cachedLines = new HashMap<>();
    }

    /**
     * Leaves current screen
     */
    public void quitCurrentScreen() {
        this.inScreen = false;
        CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§cYou left the §esession §cof the service §e" + this.screen.getService().getName() + "§c!");

        if (this.screen == null) {
            return;
        }
        this.screen.stop();
        this.screen = null;
        CloudDriver.getInstance().getCommandManager().setActive(true);
    }

    /**
     * Caches a line for a screen
     *
     * @param screen the screen
     * @param line the line
     */
    public void cache(String screen, String line) {
        List<String> list = this.cachedLines.get(screen);
        if (list == null) {
            list = new LinkedList<>();
        }

        list.add(line);
        this.cachedLines.put(screen, list);
    }

    @Override
    public void registerScreen(String name, IScreen screen) {
        map.put(name, screen);
    }

    @Override
    public List<IScreen> getScreens() {
        return new LinkedList<>(this.map.values());
    }

    /**
     * Gets a {@link ScreenObject} if its on this instance
     * by just getting it from the hashmap cache
     *
     * But if it doesn't run on this instance but on another {@link de.lystx.hytoracloud.driver.service.receiver.IReceiver}
     * for example, then it sends a request to get all cached lines of this screen and transfers them to the new screen
     *
     * @param name the name
     * @return screen output
     */
    public IScreen getOrRequest(String name) {
        if (CloudDriver.getInstance().getServiceManager().getCachedObject(name) == null) {
            return null;
        }
        if (this.map.containsKey(name)) {
            return this.map.get(name);
        } else {
            //Creating packet request and waiting for component-response
            PacketReceiverScreenRequest screenRequest = new PacketReceiverScreenRequest(name);
            Component component = screenRequest.toReply(CloudDriver.getInstance().getConnection(), 100);

            if (component != null) {
                //Request successful
                List<String> cachedLines = component.get("lines"); //Requested cached lines
                List<String> list = this.cachedLines.get(name); //This lines cached

                //New screen instance
                IScreen serviceOutput = new ScreenObject(null, null, null, name);

                if (cachedLines == null) {
                    serviceOutput.setCachedLines(list == null ? new LinkedList<>() : list);
                } else {
                    if (cachedLines.size() > (list == null ? 0 : list.size())) {
                        serviceOutput.setCachedLines(cachedLines);
                    } else {
                        serviceOutput.setCachedLines(list == null ? cachedLines : list);
                    }
                }
                return serviceOutput;
            } else {
                //Request failed no screen provided
                //Returning null...
                return null;
            }
        }
    }

}
