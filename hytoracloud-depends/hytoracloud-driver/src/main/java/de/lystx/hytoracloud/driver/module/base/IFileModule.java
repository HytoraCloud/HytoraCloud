package de.lystx.hytoracloud.driver.module.base;

import de.lystx.hytoracloud.driver.module.IModule;

import java.io.File;

public interface IFileModule extends IModule {

    /**
     * The file of this module
     *
     * @return file (location)
     */
    File getFile();

    /**
     * Sets the file of this module
     *
     * @param file the file
     */
    void setFile(File file);

}
