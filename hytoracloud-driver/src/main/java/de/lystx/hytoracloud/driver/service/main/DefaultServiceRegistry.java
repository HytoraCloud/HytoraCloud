package de.lystx.hytoracloud.driver.service.main;

import de.lystx.hytoracloud.driver.CloudDriver;

import java.util.ArrayList;
import java.util.List;

public class DefaultServiceRegistry implements IServiceRegistry {

    private final List<ICloudService> iCloudServices;
    private final List<Class<? extends ICloudService>> deniedICloudServices;

    public DefaultServiceRegistry() {
        this.iCloudServices = new ArrayList<>();
        this.deniedICloudServices = new ArrayList<>();
    }

    @Override
    public IServiceRegistry registerService(ICloudService iCloudService) {
        if (iCloudService.info() == null) {
            throw new UnsupportedOperationException("IServiceRegistry can't register ICloudService " + iCloudService.getClass().getSimpleName() + " because it doesn't have the @ICloudServiceInfo Annotation!");
        }
        this.iCloudServices.add(iCloudService);
        return this;
    }

    @Override
    public IServiceRegistry unregisterService(ICloudService iCloudService) {
        this.iCloudServices.removeIf(service -> service.getName().equalsIgnoreCase(iCloudService.getName()));
        return this;
    }

    @Override
    public <T extends ICloudService> T getInstance(Class<T> tClass) {
        if (this.deniedICloudServices.contains(tClass)) {
            try {
                throw new IllegalAccessException("The Service " + tClass.getName() + " was denied to access via " + CloudDriver.getInstance().getDriverType().name() + "!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (ICloudService ICloudService : this.iCloudServices) {
            if (ICloudService.getClass() == tClass) {
                return (T) ICloudService;
            }
        }
        return null;
    }

    @Override
    public void unregisterAll() {
        for (ICloudService registeredService : this.getRegisteredServices()) {
            this.unregisterService(registeredService);
        }
    }

    @Override
    public List<ICloudService> getRegisteredServices() {
        return this.iCloudServices;
    }

    @Override
    public List<Class<? extends ICloudService>> getDeniedToAccessServices() {
        return this.deniedICloudServices;
    }

    @Override
    public void denyService(Class<? extends ICloudService> _class) {
        this.deniedICloudServices.add(_class);
    }
}
