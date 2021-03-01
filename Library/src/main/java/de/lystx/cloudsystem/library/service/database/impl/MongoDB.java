package de.lystx.cloudsystem.library.service.database.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;
import lombok.Getter;
import org.bson.Document;

import java.util.*;

@Getter
public class MongoDB implements CloudDatabase {

    private final DatabaseService databaseService;

    private MongoDatabase database;
    private MongoClient mongoClient;

    public MongoDB(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public boolean isConnected() {
        try {
            this.mongoClient.getAddress();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void registerPlayer(CloudPlayer cloudPlayer) {
        if (!this.isRegistered(cloudPlayer.getUniqueId())) {
            CloudPlayerData data = new DefaultCloudPlayerData(cloudPlayer.getUniqueId(), cloudPlayer.getName());
            this.setPlayerData(cloudPlayer.getUniqueId(), data);
        } else {
            CloudPlayerData cloudPlayerData = this.getPlayerData(cloudPlayer.getUniqueId());
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUniqueId(), cloudPlayer.getName(), cloudPlayerData.getPermissionEntries(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart(), cloudPlayerData.getFirstLogin(), cloudPlayerData.getLastLogin());
            this.setPlayerData(cloudPlayer.getUniqueId(), newData);
        }
    }

    @Override
    public CloudPlayerData getPlayerData(UUID uuid) {
        try {
            Document document = this.getDocument("uuid", uuid);
            Document entries = document.get("permissionEntries", Document.class);
            List<PermissionEntry> permissionEntries = new LinkedList<>();
            for (String s : entries.keySet()) {
                Document sub = entries.get(s, Document.class);
                permissionEntries.add(new PermissionEntry(uuid, sub.getString("group"), sub.getString("validTime")));
            }
            return new CloudPlayerData(
                    uuid,
                    document.getString("name"),
                    permissionEntries,
                    document.get("permissions", new ArrayList<>()),
                    document.getString("ipAddress"),
                    document.getBoolean("notifyServerStart"),
                    document.get("firstLogin", Long.class),
                    document.get("lastLogin", Long.class));
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        Document entries = new Document();
        for (PermissionEntry permissionEntry : data.getPermissionEntries()) {
            uuid = permissionEntry.getUuid();
            entries.append(UUID.randomUUID().toString(), new Document().append("group", permissionEntry.getPermissionGroup()).append("validTime", permissionEntry.getValidTime()));
        }
        Document document = this.getDocument("uuid", uuid);
        Document doc = new Document();
        doc.append("uuid", uuid);
        doc.append("name", data.getName());
        doc.append("permissionEntries", entries);
        doc.append("permissions", data.getPermissions());
        doc.append("ipAddress", data.getIpAddress());
        doc.append("notifyServerStart", data.isNotifyServerStart());
        doc.append("firstLogin", data.getFirstLogin());
        doc.append("lastLogin", data.getLastLogin());
        if (document == null) {
            this.insert(doc);
        } else {
            this.insert(document, doc);
        }
    }

    @Override
    public List<CloudPlayerData> loadEntries() {
        List<CloudPlayerData> list = new LinkedList<>();
        for (Document document : this.getDocuments()) {
            CloudPlayerData data = this.getPlayerData(document.get("uuid", UUID.class));
            list.add(data);
        }
        return list;
    }

    @Override
    public void connect() {
        MongoClientURI uri = new MongoClientURI("mongodb://" + databaseService.getUsername() + ":" + databaseService.getPassword() + "@" + databaseService.getHost() + ":" + databaseService.getPort() + "/?authSource=admin");
        this.mongoClient = new MongoClient(uri);
        this.database = mongoClient.getDatabase(databaseService.getDefaultDatabase());
        if (isConnected()) {
            this.getDatabaseService().getCloudLibrary().getConsole().getLogger().sendMessage("INFO", "§aConnected to §2MongoDB Database§a!");
        } else {
            this.getDatabaseService().getCloudLibrary().getConsole().getLogger().sendMessage("INFO", "§cCouldn't connect to §eMongoDB §cDatabase!");
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isRegistered(UUID uuid) {
        return this.getDocument("uuid", uuid) != null;
    }


    /**
     * Internal Managing
     * @param document
     */



    public void insert(Document document) {
        if (!this.collectionExists(this.databaseService.getCollectionOrTable())) {
            this.getDatabaseService().getCloudLibrary().getConsole().getLogger().sendMessage("INFO", "§bCollection §3" + this.databaseService.getCollectionOrTable() + " §bdoesn't exist. §aCreating....");
            this.database.createCollection(this.databaseService.getCollectionOrTable());
            this.insert(document);
            return;
        }
        this.database.getCollection(this.databaseService.getCollectionOrTable()).insertOne(document);
    }

    public boolean collectionExists(final String collectionName) {
        for (String listCollectionName : this.database.listCollectionNames()) {
            if (listCollectionName.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }

    public List<Document> getDocuments() {
        List<Document> list = new LinkedList<>();
        try {
            this.database.getCollection(databaseService.getCollectionOrTable()).find().iterator().forEachRemaining(list::add);
        } catch (NullPointerException ignored) {
          //  this.getDatabaseService().getCloudLibrary().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't get all Documents from MongoDB §e:" + Arrays.toString(e.getStackTrace()));
        }
        return list;
    }

    public void insert(Document document, Document newDocument) {
        this.database.getCollection(this.databaseService.getCollectionOrTable()).replaceOne(document, newDocument);
    }

    public Document getDocument(String key, Object value) {
        try {
            return this.database.getCollection(this.databaseService.getCollectionOrTable()).find(Filters.eq(key, value)).first();
        } catch (NullPointerException e) {
            return null;
        }
    }

}
