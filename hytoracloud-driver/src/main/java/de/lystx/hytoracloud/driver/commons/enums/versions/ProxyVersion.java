package de.lystx.hytoracloud.driver.commons.enums.versions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter @AllArgsConstructor
public enum ProxyVersion {

    BUNGEECORD("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", "proxy.jar", 1, "bungeecord", "net.md_5.bungee.api.ProxyServer"),
    VELOCITY("https://versions.velocitypowered.com/download/1.1.8.jar", "proxy.jar", 2, "velocity", "com.velocitypowered.api.proxy.ProxyServer"),
    WATERFALL("https://papermc.io/api/v2/projects/waterfall/versions/1.16/builds/401/downloads/waterfall-1.16-401.jar", "proxy.jar", 3, "waterfall", "net.md_5.bungee.api.ProxyServer");

    private final String url;
    private final String jarName;
    private final int id;
    private final String key;
    private final String checkClass;


    /**
     * Filters for a {@link ProxyVersion} by its ID
     *
     * @param id the id
     * @return version or null
     */
    public static ProxyVersion byID(int id) {
        return Arrays.stream(values()).filter(spigot -> spigot.getId() == id).findFirst().orElse(null);
    }

    /**
     * Filters for a {@link ProxyVersion} by a key
     *
     * @param key the key
     * @return version or null
     */
    public static ProxyVersion byKey(String key) {
        return Arrays.stream(values()).filter(spigot -> spigot.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

}
