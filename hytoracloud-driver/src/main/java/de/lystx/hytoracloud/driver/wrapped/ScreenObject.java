package de.lystx.hytoracloud.driver.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.executor.ConsoleExecutor;

import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.service.screen.IScreenManager;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver.PacketReceiverScreenCache;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Getter @Setter
public class ScreenObject extends Thread implements IScreen {

    /**
     * The thread for this screen
     */
    private final Thread thread;

    /**
     * The process to get output
     */
    private final Process process;

    /**
     * The directory of the screen
     */
    private final File directory;

    /**
     * The name of the screen
     */
    private final String serviceName;

    /**
     * The already cached lines
     */
    private List<String> cachedLines;

    /**
     * The input stream
     */
    private InputStream inputStream;

    /**
     * The reader for the lines
     */
    private Scanner reader;

    /**
     * If screen is running
     */
    private boolean running;

    /**
     * The printer to print lines
     */
    private IScreenManager screenManager;

    /**
     * The console to display
     */
    private ConsoleExecutor cloudConsole;

    /**
     * Constructs a {@link ScreenObject}
     *
     * @param thread the thread
     * @param process the process
     * @param serverDir the directory
     * @param serviceName the name
     */
    public ScreenObject(Thread thread, Process process, File serverDir, String serviceName) {
        this.thread = thread;
        this.process = process;
        this.directory = serverDir;
        this.serviceName = serviceName;
        this.cachedLines = new LinkedList<>();
        this.running = false;
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
                if (CloudDriver.getInstance().getDriverType() == CloudType.RECEIVER) {
                    CloudDriver.getInstance().sendPacket(new PacketReceiverScreenCache(this.serviceName, line));
                }
                if (screenManager != null && cloudConsole != null && screenManager.getScreen() != null) {
                    if (screenManager.getScreen().getService().getName().equalsIgnoreCase(this.serviceName)) {
                        this.cloudConsole.sendMessage(this.serviceName, line);
                    }
                }
            } catch (NoSuchElementException e) {
                //No line was found in the reader.... ignoring
            }
        }
    }

    @Override
    public IService getService() {
        return CloudDriver.getInstance().getServiceManager().getCachedObject(this.serviceName);
    }

    @Override
    public void setPrinter(ConsoleExecutor console) {
        this.cloudConsole = console;
        this.screenManager = CloudDriver.getInstance().getScreenManager();
    }
}
