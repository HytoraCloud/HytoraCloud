package de.lystx.cloudsystem.library.service.module.loader;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.cloudsystem.library.service.module.ModuleInfo;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.util.HytoraClassLoader;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
public class ModuleLoader {

    private final File modulesDir;
    private final ModuleService moduleService;
    private final CloudLibrary cloudLibrary;

    public ModuleLoader(File modulesDir, ModuleService moduleService, CloudLibrary cloudLibrary) {
        this.moduleService = moduleService;
        this.cloudLibrary = cloudLibrary;
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
        if (this.getSize() == 0) {
            this.cloudLibrary.getConsole().getLogger().sendMessage("MODULES", "§cNo modules to §eload§c!");
        } else {
            try {
                this.cloudLibrary.getConsole().getLogger().sendMessage("MODULES", "§9There are §b" + this.getSize() + " §9Modules to load!");
                for (File file : Objects.requireNonNull(this.modulesDir.listFiles())) {
                    if (file.getName().endsWith(".jar")) {
                        HytoraClassLoader classLoader = new HytoraClassLoader(file);
                        VsonObject document = new VsonObject(classLoader.loadJson("config.json").toString(), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
                        if (document.isEmpty()) {
                            this.cloudLibrary.getConsole().getLogger().sendMessage("MODULES", "§cThe file §e" + file.getName() + " §cdoesn't own a §4config.json§c!");
                            return;
                        }
                        if (document.has("main") && document.has("author") && document.has("version") && document.has("name")) {
                            Class<?> cl = classLoader.findClass(document.getString("main"));
                            if (cl == null) {
                                this.cloudLibrary.getConsole().getLogger().sendMessage("MODULES", "§cThe provided MainClass of the Module §e" + file.getName() + " §ccouldn't be found!");
                                return;
                            }
                            if (cl.getSuperclass().getName().equalsIgnoreCase(Module.class.getName())) {
                                Module mod = (Module) cl.newInstance();
                                mod.setInfo(new ModuleInfo(document.getString("name"), document.getString("author"), document.getString("version"), document.getList("commands")));
                                mod.setEventService(cloudLibrary.getService(EventService.class));
                                mod.setCommandService(cloudLibrary.getService(CommandService.class));
                                mod.setCloudLibrary(this.cloudLibrary);
                                File directory = new File(this.moduleService.getModuleDir(), mod.getInfo().getName());
                                directory.mkdirs();
                                mod.setModuleDirectory(directory);
                                File file1 = new File(directory, "config.json");
                                VsonObject config = new VsonObject(file1, VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
                                if (!file1.exists()) {
                                    config.save();
                                }
                                mod.setConfig(config);
                                mod.onLoadConfig(cloudLibrary);
                                moduleService.getModules().add(mod);
                                ModuleInfo info = mod.getInfo();
                                this.cloudLibrary.getConsole().getLogger().sendMessage("MODULES", "§7The Module §b" + info.getName() + " §7by §b" + info.getAuthor() + " §7Version§8: §b" + info.getVersion() + " §7was loaded§8!");
                            } else {
                                this.cloudLibrary.getConsole().getLogger().sendMessage("MODULES", "§cThe provided MainClass of the Module §e" + file.getName() + " §cdoesn't extends the Module.class!");
                            }
                        } else {
                            this.cloudLibrary.getConsole().getLogger().sendMessage("MODULES", "§cA Module doesn't have all needed attributes in the §econfig.json§c!");
                        }
                    }
                }

            } catch (InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}
