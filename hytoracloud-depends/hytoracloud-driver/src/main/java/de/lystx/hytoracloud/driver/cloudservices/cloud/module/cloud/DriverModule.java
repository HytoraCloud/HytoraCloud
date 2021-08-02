package de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.IFileModule;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.ModuleInfo;
import de.lystx.hytoracloud.driver.commons.enums.other.ModuleCopyType;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.Getter;
import lombok.Setter;

import java.io.File;


@Setter @Getter
public abstract class DriverModule implements IFileModule {

    private static final long serialVersionUID = -1269273263236626391L;

    /**
     * The base of this module
     */
    protected IFileModule base;

    /**
     * The config
     */
    protected JsonDocument config;

    public ModuleInfo info() {
        return this.getClass().getAnnotation(ModuleInfo.class);
    }

    @Override
    public File getFile() {
        return this.base.getFile();
    }

    @Override
    public void setFile(File file) {
        this.base.setFile(file);
    }

    @Override
    public String getName() {
        return this.base.getName();
    }

    @Override
    public String[] getAuthor() {
        return this.base.getAuthor();
    }

    @Override
    public String getVersion() {
        return this.base.getVersion();
    }

    @Override
    public String getMainClass() {
        return this.base.getMainClass();
    }

    @Override
    public String getDescription() {
        return this.base.getDescription();
    }

    @Override
    public String getWebsite() {
        return this.base.getWebsite();
    }

    @Override
    public ModuleCopyType getCopyType() {
        return this.base.getCopyType();
    }

    @Override
    public void registerTasks(Object taskClassObject) {
        CloudDriver.getInstance().executeIf(() -> {
            CloudDriver.getInstance().getInstance(ModuleService.class).registerModuleTasks(this, taskClassObject);
        }, () -> CloudDriver.getInstance().getInstance(ModuleService.class) != null);
    }
}
