package de.lystx.hytoracloud.driver.utils.uuid;

import de.lystx.hytoracloud.driver.utils.utillity.CloudMap;
import io.vson.VsonValue;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;
import io.vson.manage.vson.VsonParser;
import io.vson.other.TempVsonOptions;
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
public class UUIDService {

    private final ExecutorService executor;
    private final CloudMap<String, UUID> cache;
    private final CloudMap<UUID, NameChange[]> nameChangeCache;
    private static UUIDService instance;

    /**
     * Tries to return instance of this class.
     * If it's null it will set the instance to
     * a new {@link UUIDService} with 1 Thread
     *
     * @return UUIDService
     */
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
        this.cache = new CloudMap<>();
        this.nameChangeCache = new CloudMap<>();
    }

    /**
     * Loads a UUID by name
     * (Synchronours)
     * @param playerName
     * @return
     */
    public UUID getUUID(String playerName)  {
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

            VsonObject object = new VsonObject(stringBuilder.toString());

            String uuidAsString = object.get("id").asString();

            this.cache.put(object.get("name").asString(), parseUUIDFromString(uuidAsString));
            return parseUUIDFromString(uuidAsString);
        } catch (Exception e) {
            return null;
        }
    }

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

                VsonValue element = new VsonParser(new BufferedReader(new URLReader(url)), new TempVsonOptions()).parse();
                if (!element.toString().equalsIgnoreCase("{}")) {
                    VsonArray array = element.asArray();

                    for (VsonValue vsonValue : array) {
                        VsonObject vsonObject = (VsonObject) vsonValue;

                        NameChange nameChange;

                        if (!vsonObject.has("changedToAt")) {
                            nameChange = new NameChange(vsonObject.getString("name"), -1L, true);
                            continue;
                        }
                        nameChange = new NameChange(vsonObject.getString("name"), vsonObject.getLong("changedToAt"), false);
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
            NameChange[] nameChanges = this.getNameChanges(uuid);
            String name = nameChanges[0].getName();
            this.cache.put(name, uuid);
            return name;
        } catch (Exception e) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid.toString().replace("-", ""))).openConnection();
                connection.setReadTimeout(5000);
                connection.connect();

                VsonValue vsonValue = new VsonParser(new BufferedReader(new InputStreamReader(connection.getInputStream())), new TempVsonOptions()).parse();


                if (vsonValue.toString().equals("{}")) {
                    return null;
                }

                VsonArray vsonArray = (VsonArray) vsonValue;

                VsonObject vsonObject = (VsonObject) vsonArray.get(vsonArray.size() - 1);
                String name = vsonObject.getString("name");
                UUID id = UUID.fromString(vsonObject.getString("id"));

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