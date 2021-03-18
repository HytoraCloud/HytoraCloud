package de.lystx.cloudsystem.library.service.uuid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.cloudsystem.library.service.util.ReverseMap;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UUIDService {

    private final ExecutorService executor;
    private final ReverseMap<String, UUID> cache;
    private static UUIDService instance;


    public static UUIDService getInstance() {
        if (instance == null) {
            instance = new UUIDService(1);
        }
        return instance;
    }

    /**
     * Sets the Thread amount for the Provider
     * @param threads
     */
    public UUIDService(int threads) {
        this(Executors.newFixedThreadPool(threads));
    }

    /**
     * Inits the UUIDProvider with a given
     * @param executor ExecutorService
     */
    public UUIDService(ExecutorService executor) {
        this.executor = executor;
        this.cache = new ReverseMap<>();
    }

    /**
     * Loads a UUID by name
     * (Synchronours)
     * @param playerName
     * @return
     */
    public UUID getUUID(String playerName)  {
        if (this.cache.containsKey(playerName.toUpperCase())) {
            return this.cache.get(playerName.toUpperCase());
        }
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == 400) {
                System.err.println("There is no player with the name \"" + playerName + "\"!");
                return UUID.randomUUID();
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            JsonElement element = new JsonParser().parse(bufferedReader);
            JsonObject object = element.getAsJsonObject();
            String uuidAsString = object.get("id").getAsString();

            this.cache.put(object.get("name").getAsString().toUpperCase(), parseUUIDFromString(uuidAsString));
            return parseUUIDFromString(uuidAsString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads Name from UUID
     * (Synchronous)
     * @param uuid
     * @return
     */
    public String getName(UUID uuid) {
        if (this.cache.containsValue(uuid)) {
            return this.cache.getKey(uuid);
        }
        try {
            URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if(connection.getResponseCode() == 400) {
                System.err.println("There is no player with the UUID \"" + uuid.toString() + "\"!");
                return null;
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            JsonElement element = new JsonParser().parse(bufferedReader);
            JsonArray array = element.getAsJsonArray();
            JsonObject object = array.get(0).getAsJsonObject();
            this.cache.put(object.get("name").getAsString().toUpperCase(), uuid);
            return object.get("name").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads UUID from Name
     * (Asynchronous)
     * @param playerName
     * @param consumer
     */
    public void getUUID(String playerName, Consumer<UUID> consumer) {
        if (this.cache.containsKey(playerName.toUpperCase())) {
            consumer.accept(this.cache.get(playerName.toUpperCase()));
            return;
        }
        executor.execute(() -> {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == 400) {
                    System.err.println("There is no player with the name \"" + playerName + "\"!");
                    return;
                }

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                JsonElement element = new JsonParser().parse(bufferedReader);
                JsonObject object = element.getAsJsonObject();
                String uuidAsString = object.get("id").getAsString();

                inputStream.close();
                bufferedReader.close();

                this.cache.put(playerName.toUpperCase(), parseUUIDFromString(uuidAsString));
                consumer.accept(parseUUIDFromString(uuidAsString));
            } catch (IOException e) {
                System.err.println("Couldn't connect to URL.");
                e.printStackTrace();
            }
        });
    }

    /**
     * Loads Name from UUID
     * (Asynchronous)
     * @param uuid
     * @param consumer
     */
    public void getName(UUID uuid, Consumer<String> consumer) {
        if (this.cache.containsValue(uuid)) {
            consumer.accept(this.cache.getKey(uuid));
            return;
        }
        executor.execute(() -> {
            try {
                URL url = new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid.toString().replace("-", "")));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == 400) {
                    System.err.println("There is no player with the UUID \"" + uuid.toString() + "\"!");
                    return;
                }

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                JsonElement element = new JsonParser().parse(bufferedReader);
                JsonArray array = element.getAsJsonArray();
                JsonObject object = array.get(0).getAsJsonObject();

                bufferedReader.close();
                inputStream.close();

                consumer.accept(object.get("name").getAsString());
                this.cache.put(object.get("name").getAsString().toUpperCase(), uuid);
            } catch(IOException e) {
                System.err.println("Couldn't connect to URL.");
                e.printStackTrace();
            }
        });
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