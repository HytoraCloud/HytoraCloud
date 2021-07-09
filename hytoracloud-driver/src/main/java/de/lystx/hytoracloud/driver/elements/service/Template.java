package de.lystx.hytoracloud.driver.elements.service;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.other.FileService;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.Serializable;

@Getter @AllArgsConstructor
public class Template implements Serializable {

    /**
     * The name of the template (e.g. "Lobby")
     */
    private String name;

    /**
     * The directory of this template (e.g. "local/templates/Lobby/")
     */
    private String directory;

    public Template(String group, String name, boolean b) {
        this(name, CloudDriver.getInstance().getInstance(FileService.class).getTemplatesDirectory() + "/" + group + "/" + name + "/");
    }


    public File getDirectory() {
        return new File(directory);
    }
}
