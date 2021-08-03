package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.connection.database.impl.DatabaseType;
import de.lystx.hytoracloud.driver.connection.database.IDatabase;
import de.lystx.hytoracloud.driver.connection.database.IDatabaseManager;
import lombok.Getter;

@Getter
public class CloudBridgeDatabaseManager implements IDatabaseManager {

    private IDatabase database;
    private DatabaseType databaseType;
    private String host;
    private String username;
    private String defaultDatabase;
    private String collectionOrTable;
    private String password;
    private int port;

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
