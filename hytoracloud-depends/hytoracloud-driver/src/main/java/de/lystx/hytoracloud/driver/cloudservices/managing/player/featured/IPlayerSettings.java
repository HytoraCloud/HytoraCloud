package de.lystx.hytoracloud.driver.cloudservices.managing.player.featured;

import java.util.Locale;

public interface IPlayerSettings {

    /**
     * Gets the language of the player as {@link Locale}
     *
     * @return language
     */
    Locale getLocale();

    /**
     * The view distance of the player
     *
     * @return view as byte
     */
    byte getRenderDistance();

    /**
     * Returns if the player has cape toggled
     */
    boolean hasCape();

    /**
     * Returns if the player has jacket toggled
     */
    boolean hasJacket();

    /**
     * Returns if the player has left-sleeve toggled
     */
    boolean hasLeftSleeve();

    /**
     * Returns if the player has right-sleeve toggled
     */
    boolean hasRightSleeve();

    /**
     * Returns if the player has left-pants toggled
     */
    boolean hasLeftPants();

    /**
     * Returns if the player has right-pants toggled
     */
    boolean hasRightPants();

    /**
     * Returns if the player has hat toggled
     */
    boolean hasHat();

    /**
     * If the player has chat colors
     *
     * @return boolean
     */
    boolean hasChatColors();

    /**
     * The main hand of the player
     *
     * @return main hand
     */
    MainHand getMainHand();

    /**
     * The chatmode of the player
     *
     * @return mode of chat
     */
    ChatMode getChatMode();

    enum MainHand {

        /**
         * The left hand of the player
         */
        LEFT,

        /**
         * The right hand of the player
         */
        RIGHT;

    }

    enum ChatMode {

        /**
         * The chat is shown
         */
        SHOWN,

        /**
         * Only commands will be shown
         */
        COMMANDS_ONLY,

        /**
         * Chat is completely hidden
         */
        HIDDEN;
    }
}
