package de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.uuid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import jdk.nashorn.api.scripting.URLReader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is used
 * to get the UUID or the name of a Player
 * correctly (sync and async)
 * it caches the UUID and Name in an {@link CloudMap}
 * to make it as performant as possible
 */
public class UUIDPool {

    /**
     * The executor service for performance
     */
    private final ExecutorService executor;

    /**
     * The cache name-uuid-cache
     */
    private final CloudMap<String, UUID> cache;

    /**
     * The namechange-cache
     */
    private final CloudMap<UUID, NameChange[]> nameChangeCache;

    /**
     * Sets the Thread amount for the Provider
     *
     * @param threads the amount of threads
     */
    public UUIDPool(int threads) {
        this(Executors.newFixedThreadPool(threads));
    }

    /**
     * Loads the UUIDProvider with a given
     *
     * @param executor ExecutorService
     */
    public UUIDPool(ExecutorService executor) {
        this.executor = executor;
        this.cache = new CloudMap<>();
        this.nameChangeCache = new CloudMap<>();
    }

    /**
     * Loads a UUID by name
     * If its already cached it will be loaded from cache
     * otherwise it will be loaded from web-mojang-api
     *
     * @param playerName the name of the player
     * @return uniqueId
     */
    public UUID getUniqueId(String playerName)  {
        if (this.cache.containsKey(playerName)) {
            return this.cache.get(playerName);
        }
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == 400) {
                System.err.println("There is no player with the name \"" + playerName + "\"!");
                return UUID.randomUUID();
            }


            BufferedReader bufferedReader = new BufferedReader(new URLReader(url));

            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JsonDocument object = new JsonDocument(stringBuilder.toString());

            String uuidAsString = object.getString("id");

            this.cache.put(object.getString("name"), parseUUIDFromString(uuidAsString));
            return parseUUIDFromString(uuidAsString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Loads a {@link NameChange}-Array of a given player
     *
     * @param uniqueId the uuid
     * @return namechanges
     */
    public NameChange[] getNameChanges(UUID uniqueId) {
        List<NameChange> nameChanges = new ArrayList<>();
        if (nameChangeCache.containsKey(uniqueId)) {
            return nameChangeCache.get(uniqueId);
        } else {
            try {
                URL url = new URL("https://api.mojang.com/user/profiles/" + uniqueId.toString().replace("-", "") + "/names");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == 400) {
                    System.err.println("There is no player with the UUID \"" + uniqueId.toString() + "\"!");
                    return null;
                }

                JsonElement parse = (new JsonParser()).parse(new BufferedReader(new URLReader(url)));
                
                if (!parse.toString().equalsIgnoreCase("{}")) {
                    JsonArray array = parse.getAsJsonArray();

                    for (JsonElement jsonValue : array) {
                        JsonDocument document = new JsonDocument(jsonValue.toString());

                        NameChange nameChange;

                        if (!document.has("changedToAt")) {
                            nameChange = new NameChange(document.getString("name"), -1L, true);
                            continue;
                        }
                        nameChange = new NameChange(document.getString("name"), document.getLong("changedToAt"), false);
                        nameChanges.add(nameChange);
                    }

                    this.cache.put(nameChanges.get(0).getName(), uniqueId);

                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        NameChange[] nameArray = nameChanges.toArray(new NameChange[0]);
        nameChangeCache.put(uniqueId, nameArray);
        return nameArray;
    }


    /**
     * Gets the name of a player by its uuid
     *
     * @param uuid the uuid
     * @return name
     */
    public String getName(UUID uuid) {
        if (this.cache.containsValue(uuid)) {
            return this.cache.getKey(uuid);
        }
        try {
            NameChange[] nameChanges = this.getNameChanges(uuid);
            String name = nameChanges[0].getName();
            this.cache.put(name, uuid);
            return name;
        } catch (Exception e) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid.toString().replace("-", ""))).openConnection();
                connection.setReadTimeout(5000);
                connection.connect();

                JsonElement parse = new JsonParser().parse(new BufferedReader(new InputStreamReader(connection.getInputStream())));
                
                if (parse.toString().equals("{}")) {
                    return null;
                }

                JsonArray jsonArray = (JsonArray) parse;

                JsonDocument jsonObject = new JsonDocument(jsonArray.get(jsonArray.size() - 1).toString());
                String name = jsonObject.getString("name");
                UUID id = UUID.fromString(jsonObject.getString("id"));

                cache.append(name, id);
                return name;
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
        return null;
    }


    /**
     * Shuts down the executor
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Parses a UUID to String
     * @param uuidAsString
     * @return
     */
    private UUID parseUUIDFromString(String uuidAsString) {
        String[] parts = {
                "0x" + uuidAsString.substring(0, 8),
                "0x" + uuidAsString.substring(8, 12),
                "0x" + uuidAsString.substring(12, 16),
                "0x" + uuidAsString.substring(16, 20),
                "0x" + uuidAsString.substring(20, 32)
        };

        long mostSigBits = Long.decode(parts[0]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[1]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[2]);

        long leastSigBits = Long.decode(parts[3]);
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(parts[4]);

        return new UUID(mostSigBits, leastSigBits);
    }

}