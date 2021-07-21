package de.lystx.hytoracloud.driver.cloudservices.cloud.output;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverScreenCache;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Getter @Setter
public class ServiceOutput extends Thread {

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
    private ServiceOutputPrinter screenPrinter;

    /**
     * The console to display
     */
    private CloudConsole cloudConsole;

    /**
     * Constructs a {@link ServiceOutput}
     *
     * @param thread the thread
     * @param process the process
     * @param serverDir the directory
     * @param serviceName the name
     */
    public ServiceOutput(Thread thread, Process process, File serverDir, String serviceName) {
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
                if (CloudDriver.getInstance().getDriverType() == CloudType.RECEIVER) {
                    CloudDriver.getInstance().sendPacket(new PacketReceiverScreenCache(this.serviceName, line));
                }
                this.cachedLines.add(line);
                if (screenPrinter != null && cloudConsole != null && screenPrinter.getScreen() != null) {
                    if (screenPrinter.getScreen().getServiceName().equalsIgnoreCase(this.serviceName)) {
                        this.cloudConsole.getLogger().sendMessage(this.serviceName, line);
                    }
                }
            } catch (NoSuchElementException e) {
                //Is ignored and doesn't make sense
            }
        }
    }
}
