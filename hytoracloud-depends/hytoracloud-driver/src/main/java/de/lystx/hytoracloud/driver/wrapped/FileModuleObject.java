package de.lystx.hytoracloud.driver.wrapped;

import de.lystx.hytoracloud.driver.module.base.IFileModule;
import de.lystx.hytoracloud.driver.utils.enums.other.ModuleCopyType;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter @Setter
public class FileModuleObject extends ModuleObject implements IFileModule {

    private static final long serialVersionUID = 1684513613215093404L;

    /**
     * The file of this module
     */
    private File file;

    public FileModuleObject(String name, String[] author, String description, String mainClass, String website, String version, ModuleCopyType copyType) {
        super(name, author, description, mainClass, website, version, copyType);
    }
}
