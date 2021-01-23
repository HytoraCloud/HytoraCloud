package de.lystx.cloudsystem.library.service.database.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
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
    public CloudPlayerData getPlayerData(UUID uuid) {
        try {
            Document document = this.getDocument("uuid", uuid);
            return new CloudPlayerData(
                    document.get("uuid", UUID.class),
                    document.getString("name"),
                    document.getString("permissionGroup"),
                    document.getString("tempPermissionGroup"),
                    document.getString("validadilityTime"),
                    document.get("permissions", ArrayList.class),
                    document.getString("ipAddress"),
                    document.getBoolean("notifyServerStart"),
                    document.get("firstLogin", Long.class),
                    document.get("lastLogin", Long.class)
            );
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        Document document = this.getDocument("uuid", uuid);
        Document doc = new Document();
        doc.putAll(data.getAsMap());
        if (document == null) {
            this.insert(doc);
        } else {
            this.insert(document, doc);
        }
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
    public void registerPlayer(CloudPlayer cloudPlayer) {

        if (!this.isRegistered(cloudPlayer.getUuid())) {
            CloudPlayerData data = new CloudPlayerData(
                    cloudPlayer.getUuid(),
                    cloudPlayer.getName(),
                    "Player",
                    "Player",
                    "",
                    new LinkedList<>(),
                    cloudPlayer.getIpAddress(),
                    true,
                    new Date().getTime(),
                    0L
            );
            this.setPlayerData(cloudPlayer.getUuid(), data);
        } else {
            CloudPlayerData cloudPlayerData = this.getPlayerData(cloudPlayer.getUuid());
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName(), cloudPlayerData.getPermissionGroup(), cloudPlayerData.getTempPermissionGroup(), cloudPlayerData.getValidadilityTime(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart(), cloudPlayerData.getFirstLogin(), cloudPlayerData.getLastLogin());
            this.setPlayerData(cloudPlayer.getUuid(), newData);
        }
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
        this.database.getCollection(this.databaseService.getCollectionOrTable()).insertOne(document);
    }

    public void insert(Document document, Document newDocument) {
        this.database.getCollection(this.databaseService.getCollectionOrTable()).replaceOne(document, newDocument);
    }

    public void submit(UUID uuid, String key, Object value) {
        Document document = this.getDocument("uuid", uuid);
        document.append(key, value);
        this.database.getCollection(this.databaseService.getCollectionOrTable()).updateOne(Filters.eq("uuid", uuid), new BasicDBObject("$set", new BasicDBObject(key, value)));
    }

    public Document getDocument(String key, Object value) {
        try {
            return this.database.getCollection(this.databaseService.getCollectionOrTable()).find(Filters.eq(key, value)).first();
        } catch (NullPointerException e) {
            return null;
        }
    }

}
