package de.lystx.hytoracloud.driver.service.screen;

import java.util.List;

public interface IScreenManager {

    /**
     * Gets a list of all running {@link IScreen}s
     *
     * @return list of screen
     */
    List<IScreen> getScreens();

    /**
     * Checks if in screen currently
     *
     * @return boolean
     */
    boolean isInScreen();

    /**
     * Leaves the current screen
     */
    void quitCurrentScreen();

    /**
     * Gets the current {@link IScreen}
     *
     * @return screen or null if not in any screen
     */
    IScreen getScreen();

    /**
     * Tries to get a {@link IScreen} from cache
     * If its not cached its not on this instance
     * and then a request will be send and awaits for response
     * from the instance where the screen is running on
     *
     * @param name the name of the service
     * @return screen or null if timed out or not found
     */
    IScreen getOrRequest(String name);

    /**
     * Prepares an {@link IScreen}
     *
     * @param screen the screen
     */
    void prepare(IScreen screen);

    /**
     * Caches a line
     * @param screen the screen
     *
     * @param line the line
     */
    void cache(String screen, String line);

    /**
     * Registers an {@link IScreen}
     *
     * @param name the name
     * @param screen the screen
     */
    void registerScreen(String name, IScreen screen);
}
