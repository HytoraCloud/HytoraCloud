package de.lystx.cloudsystem.library.service.screen;

import de.lystx.cloudsystem.library.service.console.CloudConsole;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Getter @Setter
public class CloudScreen {

    private final Thread thread;
    private final Process process;
    private final File serverDir;
    private final String name;
    private final List<String> cachedLines;

    private InputStream inputStream;
    private Scanner reader;

    private boolean running;

    private CloudScreenPrinter screenPrinter;
    private CloudConsole cloudConsole;

    public CloudScreen(Thread thread, Process process, File serverDir, String name) {
        this.thread = thread;
        this.process = process;
        this.serverDir = serverDir;
        this.name = name;
        this.cachedLines = new LinkedList<>();
        this.running = false;
    }

    public void start() {
        this.running = true;
        this.inputStream = process.getInputStream();
        this.reader = new Scanner(inputStream);
        while (this.running && this.inputStream != null && this.reader != null) {
            try {
                String line = reader.nextLine();
                if (line == null) {
                    continue;
                }
                this.cachedLines.add(line);
                if (screenPrinter != null && cloudConsole != null && screenPrinter.getScreen() != null) {
                    if (screenPrinter.getScreen().getName().equalsIgnoreCase(this.name)) {
                        this.cloudConsole.getLogger().sendMessage("§9[§b" + this.name + "§9]§f " + line);
                    }
                }
            } catch (NoSuchElementException ignored) {}
        }

    }

    public void stop() throws IOException {
        //this.running = false;
        //this.inputStream.close();
        //this.reader.close();
    }

}
