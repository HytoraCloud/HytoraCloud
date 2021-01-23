package de.lystx.cloudsystem.library.service.database;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.impl.Files;
import de.lystx.cloudsystem.library.service.database.impl.MongoDB;
import de.lystx.cloudsystem.library.service.database.impl.MySQL;
import de.lystx.cloudsystem.library.service.file.FileService;
import lombok.Getter;

import java.io.File;

@Getter
public class DatabaseService extends CloudService {

    private CloudDatabase database;
    private Document document;
    private String databaseType;
    private String host;
    private String username;
    private String defaultDatabase;
    private String collectionOrTable;
    private String password;
    private int port;

    public DatabaseService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.database = new Files(this);
        this.document = new Document(new File(cloudLibrary.getService(FileService.class).getDatabaseDirectory(), "database.json"));
        this.reload(document);
    }

    public void reload(Document document) {
        this.document = document;
        this.databaseType = document.getString("type", "FILES");
        this.host = document.getString("host", "127.0.0.1");
        this.username = document.getString("username", "root");
        this.password = document.getString("password", "password");
        this.port = document.getInteger("port", 3306);
        this.defaultDatabase = document.getString("defaultDatabase", "database");
        this.collectionOrTable = document.getString("collectionOrTable", "collectionOrTable");
        this.database = databaseType.equalsIgnoreCase("FILES") ? this.database = new Files(this) : databaseType.equalsIgnoreCase("MONGODB") ? new MongoDB(this) : new MySQL(this);
        document.save();
    }


}
