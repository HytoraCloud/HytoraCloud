package de.lystx.hytoracloud.driver.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter @AllArgsConstructor
public enum ProxyVersion {

    BUNGEECORD("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", "bungeeCord.jar", 1, "bungee"),
    WATERFALL("https://papermc.io/api/v2/projects/waterfall/versions/1.16/builds/401/downloads/waterfall-1.16-401.jar", "bungeeCord.jar", 2, "waterfall");

    private final String url;
    private final String jarName;
    private final int id;
    private final String key;


    public static ProxyVersion byID(int id) {
        return Arrays.stream(values()).filter(spigot -> spigot.getId() == id).findFirst().orElse(null);
    }

    public static ProxyVersion byKey(String key) {
        return Arrays.stream(values()).filter(spigot -> spigot.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

}
