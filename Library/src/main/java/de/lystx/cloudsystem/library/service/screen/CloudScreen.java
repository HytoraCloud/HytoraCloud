package de.lystx.cloudsystem.library.service.screen;

import lombok.Getter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Getter
public class CloudScreen {

    private final Thread thread;
    private final Process process;
    private final File serverDir;
    private final String name;
    private final List<String> cachedLines;

    public CloudScreen(Thread thread, Process process, File serverDir, String name) {
        this.thread = thread;
        this.process = process;
        this.serverDir = serverDir;
        this.name = name;
        this.cachedLines = new LinkedList<>();
    }

}
