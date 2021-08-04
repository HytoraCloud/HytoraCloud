package de.lystx.hytoracloud.driver.module.cloud;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.module.IModule;
import de.lystx.hytoracloud.driver.module.base.ModuleState;
import de.lystx.hytoracloud.driver.module.base.info.ModuleTask;
import de.lystx.hytoracloud.driver.module.base.info.ScheduledModuleTask;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.registry.CloudServiceInfo;
import de.lystx.hytoracloud.driver.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.event.base.HandlerMethod;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import lombok.Getter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter

@CloudServiceInfo(
        name = "ModuleService",
        description = {
                "This class loads and stores all the CloudModules"
        },
        version = 1.0
)
public class ModuleService implements ICloudService {

    private final List<DriverModule> driverModules;
    private final ModuleLoader moduleLoader;
    private final File moduleDir;
    private final Map<String, Map<Object, List<HandlerMethod<ModuleTask>>>> moduleTasks;

    public ModuleService(File moduleDir) {
        this.moduleDir = moduleDir;
        moduleDir.mkdirs();
        this.driverModules = new LinkedList<>();
        this.moduleTasks = new HashMap<>();

        this.moduleLoader = new ModuleLoader(moduleDir, this, CloudDriver.getInstance());
        this.load();
    }

    /**
     * Returns module by name
     *
     * @param name the name of the module
     * @return driver-module or null
     */
    public DriverModule getModule(String name) {
        return this.driverModules.stream().filter((module -> module.getBase().getName().equalsIgnoreCase(name))).findFirst().orElse(null);
    }

    /**
     * Registers all tasks for a {@link IModule}
     *
     * @param module the module
     * @param objectClass the class
     */
    public void registerModuleTasks(IModule module, Object objectClass) {
        List<HandlerMethod<ModuleTask>> moduleTasks = new ArrayList<>();

        for (Method m : objectClass.getClass().getDeclaredMethods()) {
            ModuleTask annotation = m.getAnnotation(ModuleTask.class);
            ScheduledModuleTask scheduledModuleTask = m.getAnnotation(ScheduledModuleTask.class);

            if (annotation != null) {
                HandlerMethod<ModuleTask> moduleTaskHandlerMethod = new HandlerMethod<>(objectClass, m, Void.class, annotation);
                if (scheduledModuleTask != null) {
                    moduleTaskHandlerMethod.setObjects(new Object[]{scheduledModuleTask});
                }
                moduleTasks.add(moduleTaskHandlerMethod);
            }
        }

        moduleTasks.sort(Comparator.comparingInt(em -> em.getAnnotation().id()));

        Map<Object, List<HandlerMethod<ModuleTask>>> listMap = this.moduleTasks.get(module.getName());
        if (listMap == null) {
            listMap = new HashMap<>();
        }
        listMap.put(objectClass, moduleTasks);
        this.moduleTasks.put(module.getName(), listMap);

    }

    /**
     * Calls all tasks for a {@link IModule}
     *
     * @param module the module
     * @param state the current state
     */
    public void callTasks(IModule module, ModuleState state) {
        Map<Object, List<HandlerMethod<ModuleTask>>> map = this.moduleTasks.get(module.getName());
        if (map == null) {
            return;
        }
        map.forEach((object, handlers) -> {
            for (HandlerMethod<ModuleTask> em : handlers) {
                if (em.getObjects() != null && em.getObjects()[0] instanceof ScheduledModuleTask) {
                    ScheduledModuleTask scheduledModuleTask = (ScheduledModuleTask)em.getObjects()[0];
                    Scheduler scheduler = CloudDriver.getInstance().getScheduler();

                    long delay = scheduledModuleTask.delay();
                    boolean sync = scheduledModuleTask.sync();
                    long repeat = scheduledModuleTask.repeat();

                    if (repeat != -1) {
                        if (sync) {
                            scheduler.scheduleRepeatingTask(() -> this.subExecute(em, state), delay, repeat);
                        } else {
                            scheduler.scheduleRepeatingTaskAsync(() -> this.subExecute(em, state), delay, repeat);
                        }
                    } else {
                        if (sync) {
                            scheduler.scheduleDelayedTask(() -> this.subExecute(em, state), delay);
                        } else {
                            scheduler.scheduleDelayedTaskAsync(() -> this.subExecute(em, state), delay);
                        }
                    }
                } else {
                    this.subExecute(em, state);
                }
            }
        });
    }

    private void subExecute(HandlerMethod<ModuleTask> em, ModuleState state) {

        if (em.getAnnotation().state() == state) {
            CloudDriver.getInstance().runTask(em.getMethod(), () -> {
                try {
                    em.getMethod().invoke(em.getListener());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    //ignoring on shutdown
                }
            });
        }
    }

    /**
     * Enables all modules
     */
    public void load() {
        this.moduleLoader.loadModules();
        for (DriverModule driverModule : this.driverModules) {
            CloudDriver.getInstance().executeIf(() -> {
                if (Arrays.asList(driverModule.info().allowedTypes()).contains(CloudDriver.getInstance().getServiceManager().getCurrentEnvironment())) {
                    this.callTasks(driverModule, ModuleState.STARTING);
                }
            }, () -> CloudDriver.getInstance().getServiceManager().getCurrentEnvironment() != ServerEnvironment.NONE);
        }
        if (CloudDriver.getInstance().getParent() != null) {
            CloudDriver.getInstance().getParent().getConsole().sendMessage("§8");
        }
    }

    /**
     * Disables all modules
     */
    public void shutdown(Runnable runnable) {
        int count = this.driverModules.size();
        if (this.driverModules.isEmpty()) {
            runnable.run();
        }
        for (DriverModule driverModule : this.driverModules) {
            if (Arrays.asList(driverModule.info().allowedTypes()).contains(CloudDriver.getInstance().getServiceManager().getCurrentEnvironment())) {
                this.callTasks(driverModule, ModuleState.STOPPING);
            }
            count--;
            if (count <= 0) {
                runnable.run();
            }
        }
    }

    @Override
    public void reload() {
        for (DriverModule driverModule : this.driverModules) {
            if (Arrays.asList(driverModule.info().allowedTypes()).contains(CloudDriver.getInstance().getServiceManager().getCurrentEnvironment())) {
                this.callTasks(driverModule, ModuleState.RELOADING);
            }
        }
    }

    @Override
    public void save() {

    }
}
