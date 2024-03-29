package de.lystx.hytoracloud.driver.connection.database.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.lystx.hytoracloud.driver.connection.database.IDatabase;

import de.lystx.hytoracloud.driver.player.permission.impl.PermissionEntry;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.player.required.OfflinePlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import org.bson.Document;

import java.util.*;

@Getter
public class DefaultDatabaseMongoDB implements IDatabase {


    private MongoDatabase database;
    private MongoClient mongoClient;

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
    public void createEntry(ICloudPlayer cloudPlayer) {
        if (!this.isRegistered(cloudPlayer.getUniqueId())) {
            OfflinePlayer data = new OfflinePlayer(cloudPlayer.getUniqueId(), cloudPlayer.getName(), Collections.singletonList(new PermissionEntry(CloudDriver.getInstance().getPermissionPool().getDefaultPermissionGroup().getName(), "")), new LinkedList<>(), cloudPlayer.getIpAddress(), true, true, new Date().getTime(), 0L, new HashMap<>());
            this.saveEntry(cloudPlayer.getUniqueId(), data);
        } else {
            OfflinePlayer offlinePlayer = this.getEntry(cloudPlayer.getUniqueId());
            OfflinePlayer newData = new OfflinePlayer(cloudPlayer.getUniqueId(), cloudPlayer.getName(), offlinePlayer.getPermissionEntries(), offlinePlayer.getExclusivePermissions(), cloudPlayer.getIpAddress(), offlinePlayer.isNotifyServerStart(), offlinePlayer.getFirstLogin(), offlinePlayer.getLastLogin());
            this.saveEntry(cloudPlayer.getUniqueId(), newData);
        }
    }

    @Override
    public OfflinePlayer getEntry(UUID uuid) {
        try {
            Document document = this.getDocument("uuid", uuid);
            Document entries = document.get("permissionEntries", Document.class);
            List<PermissionEntry> permissionEntries = new LinkedList<>();
            for (String s : entries.keySet()) {
                Document sub = entries.get(s, Document.class);
                permissionEntries.add(new PermissionEntry(sub.getString("group"), sub.getString("validTime")));
            }
            return new OfflinePlayer(
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
    public void saveEntry(UUID uuid, OfflinePlayer data) {
        Document entries = new Document();
        for (PermissionEntry permissionEntry : data.getPermissionEntries()) {
            entries.append(UUID.randomUUID().toString(), new Document().append("group", permissionEntry.getPermissionGroup()).append("validTime", permissionEntry.getValidTime()));
        }
        Document document = this.getDocument("uuid", uuid);
        Document doc = new Document();
        doc.append("uuid", uuid);
        doc.append("name", data.getName());
        doc.append("permissionEntries", entries);
        doc.append("permissions", data.getExclusivePermissions());
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
    public List<OfflinePlayer> loadEntries() {
        List<OfflinePlayer> list = new LinkedList<>();
        for (Document document : this.getDocuments()) {
            OfflinePlayer data = this.getEntry(document.get("uuid", UUID.class));
            list.add(data);
        }
        return list;
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.MONGODB;
    }

    @Override
    public void connect() {
        MongoClientURI uri = new MongoClientURI("mongodb://" + CloudDriver.getInstance().getDatabaseManager().getUsername() + ":" + CloudDriver.getInstance().getDatabaseManager().getPassword() + "@" + CloudDriver.getInstance().getDatabaseManager().getHost() + ":" + CloudDriver.getInstance().getDatabaseManager().getPort() + "/?authSource=admin");
        this.mongoClient = new MongoClient(uri);
        this.database = mongoClient.getDatabase(CloudDriver.getInstance().getDatabaseManager().getDefaultDatabase());
        if (isConnected()) {
            CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§aConnected to §2MongoDB Database§a!");
        } else {
            CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§cCouldn't connect to §eMongoDB §cDatabase!");
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
        if (!this.collectionExists(CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable())) {
            CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§bCollection §3" + CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable() + " §bdoesn't exist. §aCreating....");
            this.database.createCollection(CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable());
            this.insert(document);
            return;
        }
        this.database.getCollection(CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable()).insertOne(document);
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
            this.database.getCollection(CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable()).find().iterator().forEachRemaining(list::add);
        } catch (NullPointerException ignored) {
          //  this.getDatabaseService().getCloudLibrary().asDriver().getConsole().sendMessage("ERROR", "§cCouldn't get all Documents from MongoDB §e:" + Arrays.toString(e.getStackTrace()));
        }
        return list;
    }

    public void insert(Document document, Document newDocument) {
        this.database.getCollection(CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable()).replaceOne(document, newDocument);
    }

    public Document getDocument(String key, Object value) {
        try {
            return this.database.getCollection(CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable()).find(Filters.eq(key, value)).first();
        } catch (NullPointerException e) {
            return null;
        }
    }

}
