package de.lystx.cloudsystem.library.service.player.featured.labymod;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.server.other.process.Threader;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import jdk.nashorn.api.scripting.URLReader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor
public class LabyModAddon implements Serializable {

    private final String name = "No Name found";
    private final UUID uuid;
    private final String version = "No Version Found";
    private final String hash = "No Hash Found";
    private final int mcVersion;
    private final boolean installer;
    private final boolean restart;
    private final boolean includeInJar;
    private final String description = "No Description";
    private final int filesize;
    private final int category;
    private final boolean verified;
    private final String author = "No Author found";
    private final List<Integer> sorting;


    /**
     * Loads all {@link LabyModAddon} from
     * Website
     *
     * URL : https://dl.labymod.net/addons.json
     */
    public static void load() {
        Threader.getInstance().execute(() -> {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new URLReader(new URL("https://dl.labymod.net/addons.json")));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();

                Document document = new Document(stringBuilder.toString());

                LABY_MOD_ADDONS.addAll(document.getDocument("addons").getList("18", LabyModAddon.class));
                LABY_MOD_ADDONS.addAll(document.getDocument("addons").getList("112", LabyModAddon.class));
                LABY_MOD_ADDONS.addAll(document.getDocument("addons").getList("116", LabyModAddon.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


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

    @SneakyThrows
    public VsonObject toVson() {
        VsonObject vsonObject = new VsonObject(VsonSettings.SAFE_TREE_OBJECTS);
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            vsonObject.append(declaredField.getName(), declaredField.get(this));
        }

        return vsonObject;
    }
}
