package de.lystx.cloudsystem.library.service.database;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.database.impl.Files;
import de.lystx.cloudsystem.library.service.database.impl.MongoDB;
import de.lystx.cloudsystem.library.service.database.impl.MySQL;
import de.lystx.cloudsystem.library.service.io.FileService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
public class DatabaseService extends CloudService {

    private IDatabase database;
    private VsonObject document;
    private String databaseType;
    private String host;
    private String username;
    private String defaultDatabase;
    private String collectionOrTable;
    private String password;
    private int port;

    public DatabaseService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);
        this.database = new Files(this);
        try {
            this.document = new VsonObject(new File(cloudLibrary.getService(FileService.class).getDatabaseDirectory(), "database.json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            this.reload(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves settings
     * @param document
     */
    public void reload(VsonObject document) {
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
