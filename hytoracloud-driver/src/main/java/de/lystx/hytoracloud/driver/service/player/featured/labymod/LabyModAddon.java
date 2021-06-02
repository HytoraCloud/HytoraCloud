package de.lystx.hytoracloud.driver.service.player.featured.labymod;

import io.vson.elements.object.VsonObject;
import jdk.nashorn.api.scripting.URLReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to
 * listen to Information from {@link LabyModPlayer}s
 * and check Information of their Addons like
 * Name, author, version and stuff like that
 */
@Getter @RequiredArgsConstructor
public class LabyModAddon implements Serializable {

    private final String name;
    private final UUID uuid;
    private final String version;
    private final String hash;
    private final int mcVersion;
    private final boolean installer;
    private final boolean restart;
    private final boolean includeInJar;
    private final String description;
    private final int filesize;
    private final int category;
    private final boolean verified;
    private final String author;
    private final List<Integer> sorting;


    /**
     * Loads all {@link LabyModAddon} from
     * Website
     *
     * URL : https://dl.labymod.net/addons.json
     */
    @SneakyThrows
    public static void load() {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new URLReader(new URL("https://dl.labymod.net/addons.json")));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        bufferedReader.close();

        VsonObject document = new VsonObject(stringBuilder.toString());

        final VsonObject addons = document.getVson("addons");

        LABY_MOD_ADDONS.addAll(addons.getList("18", LabyModAddon.class));
        LABY_MOD_ADDONS.addAll(addons.getList("112", LabyModAddon.class));
        LABY_MOD_ADDONS.addAll(addons.getList("116", LabyModAddon.class));

    }

    //List to save all cached LabyModAddons to work with them later
    public static final List<LabyModAddon> LABY_MOD_ADDONS = new LinkedList<>();

    /**
     * Returns single {@link LabyModAddon}
     * filtered by Name
     *
     * @param name
     * @return
     */
    public static LabyModAddon byName(String name) {
        return LABY_MOD_ADDONS.stream().filter(addon -> addon.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns all {@link LabyModAddon} from Author
     *
     * @param author
     * @return
     */
    public static List<LabyModAddon> byAuthor(String author) {
        List<LabyModAddon> addons = new LinkedList<>();

        for (LabyModAddon labyModAddon : LABY_MOD_ADDONS) {
            if (labyModAddon.getAuthor().contains(author)) {
                addons.add(labyModAddon);
            }
        }
        return addons;
    }

}
