package de.lystx.hytoracloud.driver.service.console.progressbar;

import java.io.PrintStream;

public class InteractiveConsoleProgressBarConsumer extends ConsoleProgressBarConsumer {

    private boolean initialized = false;
    private int position = 1;

    public InteractiveConsoleProgressBarConsumer(PrintStream out) {
        super(out);
    }

    @Override
    public void accept(String str) {
        if (!initialized) {
            TerminalUtils.filterActiveConsumers(InteractiveConsoleProgressBarConsumer.class).forEach(c -> c.position++);
            TerminalUtils.activeConsumers.add(this);
            out.println(TerminalUtils.CARRIAGE_RETURN + str);
            initialized = true;
        }
        else out.print(TerminalUtils.moveCursorUp(position) + str + TerminalUtils.moveCursorDown(position));
    }

    @Override
    public void close() {
        out.flush();
        TerminalUtils.activeConsumers.remove(this);
    }
}
