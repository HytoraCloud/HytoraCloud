package de.lystx.hytoracloud.driver.commons.service;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.Serializable;

@Getter @AllArgsConstructor
public class Template implements Serializable {

    private static final long serialVersionUID = -7208858487907507144L;

    /**
     * The name of the template (e.g. "Lobby")
     */
    private final String name;

    /**
     * The directory of this template (e.g. "local/templates/Lobby/")
     */
    private final String directory;

    public Template(String group, String name, boolean b) {
        this(name, CloudDriver.getInstance().getInstance(FileService.class).getTemplatesDirectory() + "/" + group + "/" + name + "/");
    }

    /**
     * Gets the directory as file
     *
     * @return directory of template
     */
    public File getDirectory() {
        return new File(directory);
    }
}
