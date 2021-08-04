package de.lystx.hytoracloud.driver.service.minecraft.other;

import com.sun.management.OperatingSystemMXBean;
import de.lystx.hytoracloud.driver.service.IService;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * This class gives you all the information
 * on your current System like ....
 * -> CPU
 * -> Max Memory Usage
 * -> Internal CPU Usage
 * -> Free CPU
 * -> etc
 */
public class NetworkInfo {


    private long total;
    private long free;
    private long used;
    private int mb;
    private boolean calculated;

    /**
     * CPU | General
     * @return CPU Usage
     */
    public double getCPUUsage() {
        long nanoBefore = System.nanoTime();
        long cpuBefore = this.getOperatingSystemMX().getProcessCpuTime();

        long cpuAfter = this.getOperatingSystemMX().getProcessCpuTime();
        long nanoAfter = System.nanoTime();

        long percent;
        if (nanoAfter > nanoBefore) {
            percent = ((cpuAfter - cpuBefore) * 100L) / (nanoAfter - nanoBefore);
        } else {
            percent = 0;
        }

        return percent;
    }


    public void calculate() {
        Runtime runtime = Runtime.getRuntime();

        this.mb = 1024 * 1024;

        this.total = runtime.totalMemory();
        this.free = runtime.freeMemory();
        this.used = this.total - this.free;
        this.calculated = true;
    }

    public long getTotalMemory() {
        if (!this.calculated) {
            try {
                throw new IllegalAccessException("Could not access this information because NetworkInfo#calculate() wasn't called!");
            } catch (IllegalAccessException ignored) { }
        }
        return total / mb;
    }

    public long getUsedMemory() {
        return this.getOperatingSystemMX().getCommittedVirtualMemorySize();
    }

    public long getFreeMemory() {
        return this.getOperatingSystemMX().getFreePhysicalMemorySize();
    }

    public double getUsedCPU() {
        if (!this.calculated) {
            try {
                throw new IllegalAccessException("Could not access this information because NetworkInfo#calculate() wasn't called!");
            } catch (IllegalAccessException ignored) { }
        }
        return ((double) used / (double) total) * 100;
    }

    public double getFreeCPU() {
        if (!this.calculated) {
            try {
                throw new IllegalAccessException("Could not access this information because NetworkInfo#calculate() wasn't called!");
            } catch (IllegalAccessException ignored) { }
        }
        return ((double) free / (double) total) * 100;
    }

    /**
     * CPU | Internal
     * @return Internal CPU Usage
     */
    public double getInternalCPUUsage() {
        return this.getOperatingSystemMX().getProcessCpuLoad() * 100;
    }
    /**
     * Memory | System
     * @return SystemMemory
     */
    public long getSystemMemory() {
        return this.getOperatingSystemMX().getTotalPhysicalMemorySize();
    }

    /**
     * @param IServices
     * @return memory probably used by given services
     */
    public int getUsedMemory(List<IService> IServices) {
        int m = 0;
        for (IService IService : IServices) {
            m += IService.getGroup().getMemory();
        }

        return m;
    }

    /**
     * Code by CryCodes
     * @param tps
     * @return
     */
    public String formatTps(double tps) {
        return (tps >= 20.0D ? "§a" : (tps > 16.0D ? "§e" : "§c")) + (tps >= 20.0D ? "*" : "") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 60.0D);
    }

    /**
     * OS getter
     * @return OperatingSystem (Windows, Linux etc)
     */
    public OperatingSystemMXBean getOperatingSystemMX() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
    }
}
