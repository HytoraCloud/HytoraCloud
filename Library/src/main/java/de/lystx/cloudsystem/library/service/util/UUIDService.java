package de.lystx.cloudsystem.library.service.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDService {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
    private static final Map<String, UUID> uuidCache = new HashMap<String, UUID>();
    private static final Map<UUID, String> nameCache = new HashMap<UUID, String>();

    private String name;
    private UUID id;


    public static UUID getUUID(String name) {
        name = name.toLowerCase();
        if (uuidCache.containsKey(name)) {
            return uuidCache.get(name);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, System.currentTimeMillis() /1000)).openConnection();
            connection.setReadTimeout(5000);
            UUIDService data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDService.class);
            uuidCache.put(name, data.id);
            nameCache.put(data.id, data.name);
            return data.id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getName(UUID uuid) throws IOException, NullPointerException{
       if (nameCache.containsKey(uuid)) {
          return nameCache.get(uuid);
       }
      HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
      connection.setReadTimeout(5000);
      UUIDService[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDService[].class);
      UUIDService currentNameData = nameHistory[nameHistory.length - 1];
      uuidCache.put(currentNameData.name.toLowerCase(), uuid);
      nameCache.put(uuid, currentNameData.name);
      return currentNameData.name;
    }

    public String getName() {
        return name;
    }

    public static Gson getGson() {
        return gson;
    }
}
