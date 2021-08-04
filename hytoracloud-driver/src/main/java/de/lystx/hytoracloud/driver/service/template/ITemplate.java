package de.lystx.hytoracloud.driver.service.template;

import java.io.File;
import java.io.Serializable;

public interface ITemplate extends Serializable {

    /**
     * The name of this template
     *
     * @return name as string
     */
    String getName();

    /**
     * The directory of this template
     *
     * @return folder as file
     */
    File getDirectory();
}
