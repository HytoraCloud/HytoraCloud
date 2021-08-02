package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.template.ITemplate;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@Getter @AllArgsConstructor
public class TemplateObject extends WrappedObject<ITemplate, TemplateObject> implements ITemplate {

    private static final long serialVersionUID = -7208858487907507144L;

    /**
     * The name of the template (e.g. "Lobby")
     */
    private final String name;

    /**
     * The directory of this template (e.g. "local/templates/Lobby/")
     */
    private final String directory;

    public TemplateObject(String group, String name, boolean b) {
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

    @Override
    public Class<TemplateObject> getWrapperClass() {
        return TemplateObject.class;
    }

    @Override
    Class<ITemplate> getInterface() {
        return ITemplate.class;
    }
}
