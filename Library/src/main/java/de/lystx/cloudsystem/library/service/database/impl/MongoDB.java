package de.lystx.cloudsystem.library.service.database.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;
import lombok.Getter;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.util.*;

@Getter
public class MongoDB implements CloudDatabase {

    private final DatabaseService databaseService;

    private MongoDatabase database;
    private MongoClient mongoClient;

    public MongoDB(DatabaseService databaseService) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.OFF);
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
        if (!this.isRegistered(cloudPlayer.getUuid())) {
            CloudPlayerData data = new DefaultCloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName());
            this.setPlayerData(cloudPlayer.getUuid(), data);
        } else {
            CloudPlayerData cloudPlayerData = this.getPlayerData(cloudPlayer.getUuid());
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName(), cloudPlayerData.getPermissionEntries(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart(), cloudPlayerData.getFirstLogin(), cloudPlayerData.getLastLogin());
            this.setPlayerData(cloudPlayer.getUuid(), newData);
        }
    }

    @Override
    public CloudPlayerData getPlayerData(UUID uuid) {
        try {
            Document document = this.getDocument("uuid", uuid.toString());
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
        Document document = this.getDocument("uuid", uuid.toString());
        Document doc = new Document();
        doc.append("uuid", uuid.toString());
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
            CloudPlayerData data = this.getPlayerData(UUID.fromString(document.getString("uuid")));
            list.add(data);
        }
        return list;
    }

    @Override
    public void connect() {
        MongoClientURI uri = new MongoClientURI("mongodb://" + databaseService.getUsername() + ":" + databaseService.getPassword() + "@" + databaseService.getHost() + ":" + databaseService.getPort() + "/?authSource=admin");
        this.mongoClient = new MongoClient(uri);
        this.database = mongoClient.getDatabase(databaseService.getDefaultDatabase());
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isRegistered(UUID uuid) {
        return this.getDocument("uuid", uuid.toString()) != null;
    }


    /**
     * Internal Managing
     * @param document
     */


    public void insert(Document document) {
        this.database.getCollection(this.databaseService.getCollectionOrTable()).insertOne(document);
    }


    public List<Document> getDocuments() {
        List<Document> list = new LinkedList<>();
        try {
            this.database.getCollection(databaseService.getCollectionOrTable()).find().iterator().forEachRemaining(list::add);
        } catch (NullPointerException e) {}
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
