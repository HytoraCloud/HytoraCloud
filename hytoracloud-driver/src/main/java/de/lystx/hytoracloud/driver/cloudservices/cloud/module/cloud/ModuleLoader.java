package de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.IFileModule;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.ModuleInfo;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.wrapped.FileModuleObject;
import de.lystx.hytoracloud.driver.utils.HytoraClassLoader;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter @AllArgsConstructor
public class ModuleLoader {

    /**
     * The directory where modules are
     */
    private final File modulesDir;

    /**
     * The module service
     */
    private final ModuleService moduleService;

    /**
     * The cloud driver
     */
    private final CloudDriver cloudDriver;


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
    @SneakyThrows
    public void loadModules() {
        int size = this.getSize();
        if (size == 0) {
            this.cloudDriver.log("MODULES", "§cNo modules to §eload§c!");
        } else {
            this.cloudDriver.log("MODULES", "§7There " + (size == 1 ? "is" : "are")+ " §b" + size + " §7Cloud-Modules to load and enable!");
            for (File file : Objects.requireNonNull(this.modulesDir.listFiles())) {
                if (file.getName().endsWith(".jar")) {

                    HytoraClassLoader classLoader = new HytoraClassLoader(file);
                    JsonDocument document = new JsonDocument(classLoader.loadJson("config.json").toString());
                    if (document.isEmpty()) {
                        this.cloudDriver.log("MODULES", "§cThe file §e" + file.getName() + " §cdoesn't own a §4config.json§c!");
                        return;
                    }

                    if (document.has("main")) {
                        Class<?> cl = classLoader.findClass(document.getString("main"));
                        if (cl == null) {
                            this.cloudDriver.log("MODULES", "§cThe provided MainClass of the Module §e" + file.getName() + " §ccouldn't be found!");
                            return;
                        }
                        if (cl.getSuperclass().getName().equalsIgnoreCase(DriverModule.class.getName())) {
                            DriverModule cloudModule = (DriverModule) cl.newInstance();
                            ModuleInfo annotation = cloudModule.getClass().getAnnotation(ModuleInfo.class);

                            IFileModule fileModule = new FileModuleObject(
                                    annotation.name(),
                                    annotation.authors(),
                                    annotation.description(),
                                    annotation.main().getName(),
                                    annotation.website(),
                                    annotation.website(),
                                    annotation.copyType()
                            );

                            fileModule.setFile(file);
                            cloudModule.setBase(fileModule);

                            File directory = new File(this.moduleService.getModuleDir(), fileModule.getName()); directory.mkdirs();
                            JsonDocument config = new JsonDocument(new File(directory, "config.json"));
                            config.save();

                            cloudModule.setConfig(config);
                            if (Arrays.asList(cloudModule.info().allowedTypes()).contains(CloudDriver.getInstance().getServiceType())) {
                                cloudModule.onLoadConfig();
                            }

                            moduleService.getDriverModules().add(cloudModule);
                            this.cloudDriver.log("MODULES", "§7The Cloud-Module §b" + annotation.name() + " §h[§7Author§b: " + Arrays.toString(annotation.authors()) + " §7| Version§b: " + annotation.version() + " §7| Copy§b: " + annotation.copyType() + "§h] §7was loaded!");
                        } else {
                            this.cloudDriver.log("MODULES", "§cThe provided MainClass of the Module §e" + file.getName() + " §cdoesn't extends the Module.class!");
                        }
                    } else {
                        this.cloudDriver.log("MODULES", "§cA Module doesn't have the §emain-attribute in the §econfig.json§c!");
                    }


                    /*
                    Class<?> loadedClass = null;
                    URLClassLoader urlClassLoader = classLoader.classLoader();



                    JsonObject jsonObject = classLoader.loadJson("config.json");

                    if (jsonObject == null) {
                        JarFile jarFile = new JarFile(file);
                        Enumeration<JarEntry> e = jarFile.entries();

                        while (e.hasMoreElements()) {
                            JarEntry jarEntry = e.nextElement();
                            if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")){
                                continue;
                            }
                            String className = jarEntry.getName().substring(0,jarEntry.getName().length()-6);
                            className = className.replace('/', '.');

                            try {
                                loadedClass = urlClassLoader.loadClass(className);
                                if (DriverModule.class.isAssignableFrom(loadedClass)) {
                                    break;
                                }
                            } catch (ClassNotFoundException ex) {
                                //Ignoring
                            }
                        }

                    } else {
                        String main = jsonObject.get("main").getAsString();
                        loadedClass =  urlClassLoader.loadClass(main);
                    }

                    if (loadedClass == null) {
                        CloudDriver.getInstance().log("ERROR", "§cCouldn't find main-class for §e" + file.getName());
                        continue;
                    }
                    if (!DriverModule.class.isAssignableFrom(loadedClass)) {
                        CloudDriver.getInstance().log("ERROR", "§cThe class §e" + loadedClass.getName() + " §cdoes not extend §e" + DriverModule.class.getName() + "§c!");
                        continue;
                    }

                    if (loadedClass.isAnnotationPresent(ModuleInfo.class)) {
                        ModuleInfo info = loadedClass.getAnnotation(ModuleInfo.class);

                        DriverModule driverModule = (DriverModule) loadedClass.newInstance();

                        IFileModule fileModule = new FileModuleObject(
                                info.name(),
                                info.authors(),
                                info.description(),
                                info.main().getName(),
                                info.website(),
                                info.version(),
                                info.copyType()
                        );

                        fileModule.setFile(file);

                        driverModule.setBase(fileModule);

                        File directory = new File(this.moduleService.getModuleDir(), fileModule.getName()); directory.mkdirs();

                        JsonDocument config = new JsonDocument(new File(directory, "config.json"));
                        config.save();

                        driverModule.setConfig(config);

                        driverModule.onLoadConfig();

                        moduleService.getDriverModules().add(driverModule);
                        this.cloudDriver.log("MODULES", "§7The Cloud-Module §b" + info.name() + " §h[§7Author§b: " + Arrays.toString(info.authors()) + " §7| Version§b: " + info.version() + " §7| Copy§b: " + info.copyType() + "§h] §7was loaded!");
                    } else {
                        this.cloudDriver.log("MODULES", "§cThe class §e" + loadedClass.getName() + " §cof the Module §e" + file.getName() + " §cdoesn't have a §e@" + ModuleInfo.class.getSimpleName() + "-Annotation!");
                    }
                       */
                }
            }

        }
    }

}
