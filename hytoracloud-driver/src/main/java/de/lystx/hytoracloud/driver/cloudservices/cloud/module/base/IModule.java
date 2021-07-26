package de.lystx.hytoracloud.driver.cloudservices.cloud.module.base;

import de.lystx.hytoracloud.driver.commons.enums.other.ModuleCopyType;

import java.io.Serializable;

public interface IModule extends Serializable {

    /**
     * The name of this module
     *
     * @return name
     */
    String getName();

    /**
     * The author of this module
     *
     * @return author
     */
    String[] getAuthor();

    /**
     * The version of this module
     * @return
     */
    String getVersion();

    /**
     * The main-class of this module
     *
     * @return main class
     */
    String getMainClass();

    /**
     * The description of this module
     *
     * @return description
     */
    String getDescription();

    /**
     * An additional info for this module
     * (Website)
     *
     * @return info
     */
    String getWebsite();

    /**
     * The copy type (where it should be copied)
     *
     * @return type
     */
    ModuleCopyType getCopyType();
}
