package de.lystx.cloudsystem.library.service.module.loader;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import de.lystx.cloudsystem.library.service.module.Module;
import de.lystx.cloudsystem.library.service.module.ModuleInfo;
import de.lystx.cloudsystem.library.service.module.ModuleService;
import de.lystx.cloudsystem.library.service.util.HytoraClassLoader;
import lombok.Getter;

import java.io.File;
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

    public int getSize() {
        int ret = 0;
        for (File file : Objects.requireNonNull(this.modulesDir.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                ret++;
            }
        }
        return ret;
    }

    public void loadModules() {
        if (this.getSize() == 0) {
            this.cloudLibrary.getConsole().getLogger().sendMessage("MODULE", "§cNo modules to §eload§c!");
        } else {
            try {
                for (File file : Objects.requireNonNull(this.modulesDir.listFiles())) {
                    if (file.getName().endsWith(".jar")) {
                        HytoraClassLoader classLoader = new HytoraClassLoader(file);
                        Document document = new Document(classLoader.loadJson("config.json"));
                        if (document.isEmpty()) {
                            this.cloudLibrary.getConsole().getLogger().sendMessage("MODULE", "§cThe file §e" + file.getName() + " §cdoesn't own a §4config.json§c!");
                            return;
                        }
                        if (document.has("main") && document.has("author") && document.has("version") && document.has("name")) {
                            Class<?> cl = classLoader.findClass(document.getString("main"));
                            if (cl == null) {
                                this.cloudLibrary.getConsole().getLogger().sendMessage("MODULE", "§cThe provided MainClass of the Module §e" + file.getName() + " §ccouldn't be found!");
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
                                mod.setConfig(new Document(new File(directory, "config.json")));
                                moduleService.getModules().add(mod);
                                ModuleInfo info = mod.getInfo();
                                this.cloudLibrary.getConsole().getLogger().sendMessage("MODULE", "§7The Module §c" + info.getName() + " §7by §c" + info.getAuthor() + " §7Version§8: §c" + info.getVersion() + " §7was loaded§8!");
                            } else {
                                this.cloudLibrary.getConsole().getLogger().sendMessage("MODULE", "§cThe provided MainClass of the Module §e" + file.getName() + " §cdoesn't extends the Module.class!");
                            }
                        } else {
                            this.cloudLibrary.getConsole().getLogger().sendMessage("MODULE", "§cA Module doesn't have all needed attributes in the §econfig.json§c!");
                        }
                    }
                }

            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
