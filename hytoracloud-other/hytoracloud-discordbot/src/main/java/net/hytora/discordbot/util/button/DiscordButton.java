package net.hytora.discordbot.util.button;

import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.internal.interactions.ButtonImpl;
import net.hytora.discordbot.Hytora;

import java.util.function.Consumer;

public class DiscordButton {

    /**
     * The ID of this button (e.g. 0x00)
     */
    private final int id;

    /**
     * The display (e.g. "Click me")
     */
    private final String display;

    /**
     * The style
     */
    private final ButtonStyle style;

    /**
     * The consumer to work with
     */
    private final Consumer<DiscordButtonAction> actionConsumer;

    public DiscordButton(int id, String display, ButtonStyle style, Consumer<DiscordButtonAction> actionConsumer) {
        this.id = id;
        this.display = display;
        this.style = style;
        this.actionConsumer = actionConsumer;
    }

    /**
     * Creates the {@link Button}
     * and adds the button to the cache
     *
     * @return button
     */
    public Button submit() {
        Hytora.getHytora().getDiscordButtons().add(this);
        return new ButtonImpl(String.valueOf(id), display, style, false, null);
    }

    public int getId() {
        return id;
    }

    public Consumer<DiscordButtonAction> getActionConsumer() {
        return actionConsumer;
    }
}
