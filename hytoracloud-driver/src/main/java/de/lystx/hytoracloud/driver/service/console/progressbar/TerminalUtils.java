package de.lystx.hytoracloud.driver.service.console.progressbar;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;


public class TerminalUtils {

    static final char CARRIAGE_RETURN = '\r';
    static final char ESCAPE_CHAR = '\u001b';
    static final int DEFAULT_TERMINAL_WIDTH = 80;

    private static Terminal terminal = null;
    private static boolean cursorMovementSupported = false;

    static Queue<ProgressBarConsumer> activeConsumers = new ConcurrentLinkedQueue<>();

    synchronized static int getTerminalWidth() {
        Terminal terminal = getTerminal();
        int width = terminal.getWidth();

        return (width >= 10) ? width : DEFAULT_TERMINAL_WIDTH;
    }

    static boolean hasCursorMovementSupport() {
        if (terminal == null)
            terminal = getTerminal();
        return cursorMovementSupported;
    }

    synchronized static void closeTerminal() {
        try {
            if (terminal != null) {
                terminal.close();
                terminal = null;
            }
        } catch (IOException ignored) { /* noop */ }
    }

    static <T extends ProgressBarConsumer> Stream<T> filterActiveConsumers(Class<T> clazz) {
        return activeConsumers.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    static String moveCursorUp(int count) {
        return ESCAPE_CHAR + "[" + count + "A" + CARRIAGE_RETURN;
    }

    static String moveCursorDown(int count) {
        return ESCAPE_CHAR + "[" + count + "B" + CARRIAGE_RETURN;
    }


    static Terminal getTerminal() {
        if (terminal == null) {
            try {
                terminal = TerminalBuilder.builder().dumb(true).build();
                cursorMovementSupported = (
                        terminal.getStringCapability(InfoCmp.Capability.cursor_up) != null &&
                        terminal.getStringCapability(InfoCmp.Capability.cursor_down) != null
                );
            } catch (IOException e) {
                throw new RuntimeException("This should never happen! Dumb terminal should have been created.");
            }
        }
        return terminal;
    }

}
