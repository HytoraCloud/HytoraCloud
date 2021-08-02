package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class SkinFetcher {

    /**
     * All cached values for name
     */
    private final Map<String, String> values;

    /**
     * All cached signatures for name
     */
    private final Map<String, String> signatures;

    /**
     * All cached skins
     */
    private final List<String> skins;

    public SkinFetcher() {
        this.values = new HashMap<>();
        this.signatures = new HashMap<>();
        this.skins = new LinkedList<>();
    }

    /**
     * Gets the value of a skin
     *
     * @param uuid the uuid
     * @return value
     */
    public String getSkinValue(String uuid) {
        return data(uuid)[0];
    }
    /**
     * Gets the signature of a skin
     *
     * @param uuid the uuid
     * @return signature
     */
    public String getSkinSignature(String uuid) {
        return data(uuid)[1];
    }

    /**
     * Fetches the data from web for a given uuid
     *
     * @param uuid the uuid
     * @return string array with data
     */
    public String[] data(String uuid) {
        String[] data = new String[2];
        if (!skins.contains(uuid)) {
            skins.add(uuid);
            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                URLConnection uc = url.openConnection();
                uc.setUseCaches(false);
                uc.setDefaultUseCaches(false);
                uc.addRequestProperty("User-Agent", "Mozilla/5.0");
                uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                uc.addRequestProperty("Pragma", "no-cache");
                String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(json);
                JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
                for (int i = 0; i < properties.size(); i++) {
                    try {
                        JSONObject property = (JSONObject) properties.get(i);
                        String name = (String) property.get("name");
                        String value = (String) property.get("value");
                        String signature = property.containsKey("signature") ? (String) property.get("signature") : null;
                        values.put(uuid, value);
                        signatures.put(uuid, signature);
                        data[0] = value;
                        data[1] = signature;
                    } catch (Exception e) {}
                }
            } catch (Exception e) {}
        } else {
            data[0] = values.get(uuid);
            data[1] = signatures.get(uuid);
        }
        return data;
    }

}
