package de.lystx.hytoracloud.driver.service.screen;

import de.lystx.hytoracloud.driver.service.console.CloudConsole;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Getter @Setter
public class CloudScreen extends Thread {

    private final Thread thread;
    private final Process process;
    private final File serverDir;
    private final String screenName;
    private final List<String> cachedLines;

    private InputStream inputStream;
    private Scanner reader;

    private boolean running;

    private CloudScreenPrinter screenPrinter;
    private CloudConsole cloudConsole;

    private boolean runningOnThisCloudInstance;

    public CloudScreen(Thread thread, Process process, File serverDir, String screenName) {
        this.thread = thread;
        this.process = process;
        this.serverDir = serverDir;
        this.screenName = screenName;
        this.cachedLines = new LinkedList<>();
        this.running = false;
        this.runningOnThisCloudInstance = true;
    }

    /**
     * Declares that this screen is running
     * on another CloudInstance for example
     * another Receiver
     */
    public void notRunningOnThisInstance() {
        this.runningOnThisCloudInstance = false;
    }


    /**
     * Starts the screen printing
     * Checks if the current screen is this
     * if (true) > Prints line
     * And caches line
     */
    public void run() {
        this.running = true;
        this.inputStream = process.getInputStream();
        this.reader = new Scanner(inputStream, "UTF-8");
        while (this.running && this.inputStream != null && this.reader != null) {
            try {
                String line = reader.nextLine();
                if (line == null) {
                    continue;
                }
                this.cachedLines.add(line);
                if (screenPrinter != null && cloudConsole != null && screenPrinter.getScreen() != null) {
                    if (screenPrinter.getScreen().getScreenName().equalsIgnoreCase(this.screenName)) {
                        this.cloudConsole.getLogger().sendMessage(this.screenName, line);
                    }
                }
            } catch (NoSuchElementException e) {
                //Is ignored and doesn't make sense
            }
        }
    }
}
