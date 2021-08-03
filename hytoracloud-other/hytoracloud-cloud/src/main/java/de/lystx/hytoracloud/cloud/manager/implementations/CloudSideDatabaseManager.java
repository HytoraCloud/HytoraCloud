package de.lystx.hytoracloud.cloud.manager.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.connection.database.impl.DatabaseType;
import de.lystx.hytoracloud.driver.connection.database.IDatabase;
import de.lystx.hytoracloud.driver.connection.database.IDatabaseManager;
import de.lystx.hytoracloud.driver.connection.database.impl.DefaultDatabaseFiles;
import de.lystx.hytoracloud.driver.registry.CloudServiceInfo;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
@CloudServiceInfo(
        name = "DatabaseService",
        description = {
                "This service manages the database"
        },
        version = 1.3
)
public class CloudSideDatabaseManager implements ICloudService, IDatabaseManager {

    private IDatabase database;
    private JsonObject<?> jsonDocument;
    private DatabaseType databaseType;
    private String host;
    private String username;
    private String defaultDatabase;
    private String collectionOrTable;
    private String password;
    private int port;

    public CloudSideDatabaseManager() {
        this.database = new DefaultDatabaseFiles();
        File file = new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getDatabaseDirectory(), "database.json");
        if (file.exists()) {
            this.jsonDocument = new JsonDocument(file);
        } else {
            try {
                file.createNewFile();
                this.jsonDocument = new JsonDocument(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.reload(jsonDocument);
    }

    /**
     * Saves settings
     * @param jsonDocument
     */
    public void reload(JsonObject<?> jsonDocument) {

        this.load(
                jsonDocument.def("127.0.0.1").getString("host"),
                jsonDocument.def(3306).getInteger("port"),
                jsonDocument.def("root").getString("username"),
                jsonDocument.def("pw").getString("password"),
                jsonDocument.def("value").getString("collectionOrTable"),
                jsonDocument.def("database").getString("defaultDatabase"),
                DatabaseType.valueOf(jsonDocument.def("FILES").getString("type"))
        );

        this.jsonDocument = jsonDocument;
        jsonDocument.save();
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
