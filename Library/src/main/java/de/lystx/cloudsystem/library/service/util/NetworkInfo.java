package de.lystx.cloudsystem.library.service.util;

import com.sun.management.OperatingSystemMXBean;
import de.lystx.cloudsystem.library.elements.service.Service;

import java.lang.management.ManagementFactory;
import java.util.List;

public class NetworkInfo {



    public double getCPUUsage() {
        return this.getOperatingSystemMX().getSystemCpuLoad() * 100;
    }

    public double getInternalCPUUsage() {
        return this.getOperatingSystemMX().getProcessCpuLoad() * 100;
    }

    public long getSystemMemory() {
        return this.getOperatingSystemMX().getTotalPhysicalMemorySize();
    }

    public int getUsedMemory(List<Service> services) {
        int m = 0;
        for (Service service : services) {
            m += service.getServiceGroup().getMaxRam();
        }

        return m;
    }

    public OperatingSystemMXBean getOperatingSystemMX() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
    }
}
