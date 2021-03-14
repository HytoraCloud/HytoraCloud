package de.lystx.cloudsystem.library.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter @AllArgsConstructor
public enum Spigot {

    V1_8_8("https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar", "spigot-1.8.8-R0.1-SNAPSHOT-latest.jar", 1, "1.8.8"),
    V1_9("https://cdn.getbukkit.org/spigot/spigot-1.9-R0.1-SNAPSHOT-latest.jar", "spigot-1.9-R0.1-SNAPSHOT-latest.jar", 2, "1.9"),
    V1_9_2("https://cdn.getbukkit.org/spigot/spigot-1.9.2-R0.1-SNAPSHOT-latest.jar", "spigot-1.9.2-R0.1-SNAPSHOT-latest.jar", 3, "1.9.2"),
    V1_9_4("https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar", "spigot-1.9.4-R0.1-SNAPSHOT-latest.jar", 4, "1.9.4"),
    V1_10("https://cdn.getbukkit.org/spigot/spigot-1.10-R0.1-SNAPSHOT-latest.jar", "spigot-1.10-R0.1-SNAPSHOT-latest.jar", 5, "1.10"),
    V1_10_2("https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar", "spigot-1.10.2-R0.1-SNAPSHOT-latest.jar", 6, "1.10.2"),
    V1_11("https://cdn.getbukkit.org/spigot/spigot-1.11.jar", "spigot-1.11.jar", 7, "1.11"),
    V1_11_1("https://cdn.getbukkit.org/spigot/spigot-1.11.1.jar", "spigot-1.11.1.jar", 8, "1.11.1"),
    V1_11_2("https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar", "spigot-1.11.2.jar", 9, "1.11.2"),
    V1_12("https://cdn.getbukkit.org/spigot/spigot-1.12.jar", "spigot-1.12.jar", 10, "1.12"),
    V1_12_1("https://cdn.getbukkit.org/spigot/spigot-1.12.1.jar", "spigot-1.12.1.jar", 11, "1.12.1"),
    V1_12_2("https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar", "spigot-1.12.2.jar", 12, "1.12.2"),
    V1_13("https://cdn.getbukkit.org/spigot/spigot-1.13.jar", "spigot-1.13.jar", 13, "1.13"),
    V1_13_1("https://cdn.getbukkit.org/spigot/spigot-1.13.1.jar", "spigot-1.13.1.jar", 14, "1.13.1"),
    V1_13_2("https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar", "spigot-1.13.2.jar", 15, "1.13.2"),
    V1_14("https://cdn.getbukkit.org/spigot/spigot-1.14.jar", "spigot-1.14.jar", 16, "1.14"),
    V1_14_1("https://cdn.getbukkit.org/spigot/spigot-1.14.1.jar", "spigot-1.14.1.jar", 17, "1.14.1"),
    V1_14_2("https://cdn.getbukkit.org/spigot/spigot-1.14.2.jar", "spigot-1.14.2.jar", 18, "1.14.2"),
    V1_14_3("https://cdn.getbukkit.org/spigot/spigot-1.14.3.jar", "spigot-1.14.3.jar", 19, "1.14.3"),
    V1_14_4("https://cdn.getbukkit.org/spigot/spigot-1.14.4.jar", "spigot-1.14.4.jar", 20, "1.14.4"),
    V1_15("https://cdn.getbukkit.org/spigot/spigot-1.15.jar", "spigot-1.15.jar", 21, "1.15"),
    V1_15_1("https://cdn.getbukkit.org/spigot/spigot-1.15.1.jar", "spigot-1.15.1.jar", 22, "1.15.1"),
    V1_15_2("https://cdn.getbukkit.org/spigot/spigot-1.15.2.jar", "spigot-1.15.2.jar", 23, "1.15.2"),
    V1_16_1("https://cdn.getbukkit.org/spigot/spigot-1.16.1.jar", "spigot-1.16.1.jar", 24, "1.16.1"),
    V1_16_2("https://cdn.getbukkit.org/spigot/spigot-1.16.2.jar", "spigot-1.16.2.jar", 25, "1.16.2"),
    V1_16_3("https://cdn.getbukkit.org/spigot/spigot-1.16.3.jar", "spigot-1.16.3.jar", 26, "1.16.3"),
    V1_16_4("https://cdn.getbukkit.org/spigot/spigot-1.16.4.jar", "spigot-1.16.4.jar", 27, "1.16.4"),
    V1_16_5("https://cdn.getbukkit.org/spigot/spigot-1.16.5.jar", "spigot-1.16.5.jar", 28, "1.16.5");

    private final String url;
    private final String jarName;
    private final int id;
    private final String key;


    public static Spigot byID(int id) {
        return Arrays.stream(values()).filter(spigot -> spigot.getId() == id).findFirst().orElse(null);
    }

    public static Spigot byKey(String key) {
        return Arrays.stream(values()).filter(spigot -> spigot.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

}
