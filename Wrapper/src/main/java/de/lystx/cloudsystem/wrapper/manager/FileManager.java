package de.lystx.cloudsystem.wrapper.manager;

import de.lystx.cloudsystem.wrapper.Wrapper;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Getter
public class FileManager {

    private final Wrapper wrapper;

    private final File wrapperDirectory;
    private final File templatesDirectory;
    private final File globalDirectory;
    private final File versionsDirectory;
    private final File pluginsDirectory;
    private final File spigotPluginsDirectory;
    private final File bungeeCordPluginsDirectory;


    private final File serverDirectory;
    private final File staticServerDirectory;
    private final File dynamicServerDirectory;

    public FileManager(Wrapper wrapper) {
        this.wrapper = wrapper;

        this.wrapperDirectory = new File("./local");
        this.templatesDirectory = new File(this.wrapperDirectory, "templates/");
        this.globalDirectory = new File(this.wrapperDirectory, "global/");
        this.versionsDirectory = new File(this.wrapperDirectory, "versions/");
        this.pluginsDirectory = new File(this.globalDirectory, "plugins/");
        this.spigotPluginsDirectory = new File(this.pluginsDirectory, "spigot/");
        this.bungeeCordPluginsDirectory = new File(this.pluginsDirectory, "bungee/");

        this.serverDirectory = new File(this.wrapperDirectory, "server/");
        this.staticServerDirectory = new File(this.serverDirectory, "static/");
        this.dynamicServerDirectory = new File(this.serverDirectory, "dynamic/");

        this.wrapperDirectory.mkdirs();
        this.templatesDirectory.mkdirs();
        this.globalDirectory.mkdirs();
        this.versionsDirectory.mkdirs();

        this.pluginsDirectory.mkdirs();
        this.spigotPluginsDirectory.mkdirs();
        this.bungeeCordPluginsDirectory.mkdirs();

        this.serverDirectory.mkdirs();
        this.staticServerDirectory.mkdirs();
        this.dynamicServerDirectory.mkdirs();

    }


    public void copyFileWithURL(String filename, File location) {
        try {
            URL inputUrl = getClass().getResource(filename);
            if (location.exists()) {
                return;
            }
            try {
                FileUtils.copyURLToFile(inputUrl, location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) { }
    }
}
