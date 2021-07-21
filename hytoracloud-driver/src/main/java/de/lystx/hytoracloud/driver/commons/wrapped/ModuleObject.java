package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.IModule;
import de.lystx.hytoracloud.driver.commons.enums.other.ModuleCopyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ModuleObject extends WrappedObject<IModule, ModuleObject> implements IModule {

    private static final long serialVersionUID = 1684513613215093404L;

    /**
     * The name
     */
    private String name;

    /**
     * The author
     */
    private String author;

    /**
     * The description
     */
    private String description;

    /**
     * The mainclass
     */
    private String mainClass;

    /**
     * The website
     */
    private String website;

    /**
     * The version
     */
    private String version;

    /**
     * The copy type
     */
    private ModuleCopyType copyType;

    @Override
    public Class<ModuleObject> getWrapperClass() {
        return ModuleObject.class;
    }

    @Override
    Class<IModule> getInterface() {
        return IModule.class;
    }
}
