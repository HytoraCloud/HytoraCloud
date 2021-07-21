package de.lystx.hytoracloud.launcher.cloud.impl.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.DatabaseType;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabase;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabaseManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.impl.DefaultDatabaseFiles;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
@ICloudServiceInfo(
        name = "DatabaseService",
        type = CloudServiceType.CONFIG,
        description = {
                "This service manages the database"
        },
        version = 1.3
)
public class CloudSideDatabaseManager implements ICloudService, IDatabaseManager {

    private IDatabase database;
    private JsonEntity jsonEntity;
    private DatabaseType databaseType;
    private String host;
    private String username;
    private String defaultDatabase;
    private String collectionOrTable;
    private String password;
    private int port;

    public CloudSideDatabaseManager() {
        this.database = new DefaultDatabaseFiles();
        File file = new File(CloudDriver.getInstance().getInstance(FileService.class).getDatabaseDirectory(), "database.json");
        if (file.exists()) {
            this.jsonEntity = new JsonEntity(file);
        } else {
            try {
                file.createNewFile();
                this.jsonEntity = new JsonEntity(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.reload(jsonEntity);
    }

    /**
     * Saves settings
     * @param jsonEntity
     */
    public void reload(JsonEntity jsonEntity) {

        this.load(
                jsonEntity.getString("host", "127.0.0.1"),
                jsonEntity.getInteger("port", 3306),
                jsonEntity.getString("username", "root"),
                jsonEntity.getString("password", "pw"),
                jsonEntity.getString("collectionOrTable", "value"),
                jsonEntity.getString("defaultDatabase", "database"),
                DatabaseType.valueOf(jsonEntity.getString("type", "FILES"))
        );

        this.jsonEntity = jsonEntity;
        jsonEntity.save();
    }

    @Override
    public void load(String host, int port, String username, String password, String collectionOrTable, String defaultDatabase, DatabaseType type) {

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.collectionOrTable = collectionOrTable;
        this.defaultDatabase = defaultDatabase;
        this.databaseType = type;

        this.setDatabase(databaseType);
    }

    @Override
    public void setDatabase(DatabaseType databaseType) {
        database = DatabaseType.getDatabaseFromType(databaseType);
    }


    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
