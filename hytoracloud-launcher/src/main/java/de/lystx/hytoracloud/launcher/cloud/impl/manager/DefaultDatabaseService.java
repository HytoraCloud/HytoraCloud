package de.lystx.hytoracloud.launcher.cloud.impl.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.database.DatabaseType;
import de.lystx.hytoracloud.driver.service.database.IDatabase;
import de.lystx.hytoracloud.driver.service.database.IDatabaseManager;
import de.lystx.hytoracloud.driver.service.database.impl.DefaultDatabaseFiles;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.other.FileService;
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
public class DefaultDatabaseService implements ICloudService, IDatabaseManager {

    private IDatabase database;
    private JsonBuilder jsonBuilder;
    private DatabaseType databaseType;
    private String host;
    private String username;
    private String defaultDatabase;
    private String collectionOrTable;
    private String password;
    private int port;

    public DefaultDatabaseService() {
        this.database = new DefaultDatabaseFiles();
        File file = new File(CloudDriver.getInstance().getInstance(FileService.class).getDatabaseDirectory(), "database.json");
        if (file.exists()) {
            this.jsonBuilder = new JsonBuilder(file);
        } else {
            try {
                file.createNewFile();
                this.jsonBuilder = new JsonBuilder(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.reload(jsonBuilder);
    }

    /**
     * Saves settings
     * @param jsonBuilder
     */
    public void reload(JsonBuilder jsonBuilder) {

        this.load(
                jsonBuilder.getString("host", "127.0.0.1"),
                jsonBuilder.getInteger("port", 3306),
                jsonBuilder.getString("username", "root"),
                jsonBuilder.getString("password", "pw"),
                jsonBuilder.getString("collectionOrTable", "value"),
                jsonBuilder.getString("defaultDatabase", "database"),
                DatabaseType.valueOf(jsonBuilder.getString("type", "FILES"))
        );

        this.jsonBuilder = jsonBuilder;
        jsonBuilder.save();
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


}
