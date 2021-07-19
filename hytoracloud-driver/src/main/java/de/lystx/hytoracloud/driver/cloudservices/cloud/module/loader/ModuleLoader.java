package de.lystx.hytoracloud.driver.cloudservices.cloud.module.loader;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.Module;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.ModuleCopyType;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.ModuleInfo;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.ModuleService;
import utillity.HytoraClassLoader;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

@Getter
public class ModuleLoader {

    private final File modulesDir;
    private final ModuleService moduleService;
    private final CloudDriver cloudDriver;

    public ModuleLoader(File modulesDir, ModuleService moduleService, CloudDriver cloudDriver) {
        this.moduleService = moduleService;
        this.cloudDriver = cloudDriver;
        this.modulesDir = modulesDir;
    }


    /**
     * Ignoring al files that don't end with ".jar" (folders etc)
     * @return amount of modules
     */
    public int getSize() {
        int i = 0;
        for (File file : Objects.requireNonNull(this.modulesDir.listFiles())) {
            if (!file.isFile()) {
                continue;
            }
            if (file.getName().endsWith(".jar")) {
                i++;
            }
        }
        return i;
    }

    /**
     * Loads all modules
     */
    public void loadModules() {
        int size = this.getSize();
        if (size == 0) {
            this.cloudDriver.getParent().getConsole().getLogger().sendMessage("MODULES", "§cNo modules to §eload§c!");
        } else {
            try {
                this.cloudDriver.getParent().getConsole().getLogger().sendMessage("MODULES", "§7There " + (size == 1 ? "is" : "are")+ " §b" + size + " §7Cloud-Modules to load and enable!");
                for (File file : Objects.requireNonNull(this.modulesDir.listFiles())) {
                    if (file.getName().endsWith(".jar")) {
                        HytoraClassLoader classLoader = new HytoraClassLoader(file);
                        VsonObject document = new VsonObject(classLoader.loadJson("config.json").toString(), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
                        if (document.isEmpty()) {
                            this.cloudDriver.getParent().getConsole().getLogger().sendMessage("MODULES", "§cThe file §e" + file.getName() + " §cdoesn't own a §4config.json§c!");
                            return;
                        }
                        if (document.has("main") && document.has("author") && document.has("version") && document.has("name") && document.has("copyType")) {
                            Class<?> cl = classLoader.findClass(document.getString("main"));
                            if (cl == null) {
                                this.cloudDriver.getParent().getConsole().getLogger().sendMessage("MODULES", "§cThe provided MainClass of the Module §e" + file.getName() + " §ccouldn't be found!");
                                return;
                            }
                            if (cl.getSuperclass().getName().equalsIgnoreCase(Module.class.getName())) {
                                Module mod = (Module) cl.newInstance();
                                final ModuleInfo moduleInfo = new ModuleInfo(document.getString("name"), document.getString("author"), document.getString("version"), new LinkedList<>(), ModuleCopyType.valueOf(document.getString("copyType").toUpperCase()), file);
                                mod.setInfo(moduleInfo);
                                File directory = new File(this.moduleService.getModuleDir(), mod.getInfo().getName());
                                directory.mkdirs();
                                mod.setModuleDirectory(directory);
                                File file1 = new File(directory, "config.json");
                                VsonObject config = new VsonObject(file1, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
                                if (!file1.exists()) {
                                    config.save();
                                }
                                mod.setConfig(config);
                                mod.onLoadConfig();
                                mod.onReload();
                                moduleService.getModules().add(mod);
                                ModuleInfo info = mod.getInfo();
                                this.cloudDriver.getParent().getConsole().getLogger().sendMessage("MODULES", "§7The Cloud-Module §b" + info.getName() + " §h[§7Author§b: " + info.getAuthor() + " §7| Version§b: " + info.getVersion() + " §7| Copy§b: " + info.getCopyType() + "§h] §7was loaded!");
                            } else {
                                this.cloudDriver.getParent().getConsole().getLogger().sendMessage("MODULES", "§cThe provided MainClass of the Module §e" + file.getName() + " §cdoesn't extends the Module.class!");
                            }
                        } else {
                            this.cloudDriver.getParent().getConsole().getLogger().sendMessage("MODULES", "§cA Module doesn't have all needed attributes in the §econfig.json§c!");
                            String needed = Arrays.toString(ModuleInfo.class.getDeclaredFields()).replace("[", "").replace("]", "");
                            this.cloudDriver.getParent().getConsole().getLogger().sendMessage("MODULES", "§cNeeded§h: §e" + needed);
                        }
                    }
                }

            } catch (InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}
