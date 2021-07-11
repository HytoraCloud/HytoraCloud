package de.lystx.hytoracloud.driver.service.managing.database;

import de.lystx.hytoracloud.driver.service.managing.database.impl.DefaultDatabaseFiles;
import de.lystx.hytoracloud.driver.service.managing.database.impl.DefaultDatabaseMongoDB;
import de.lystx.hytoracloud.driver.service.managing.database.impl.DefaultDatabaseMysQL;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor @Getter
public enum DatabaseType {

    /**
     * Everything is stored in Files
     * and directories no database needed
     */
    FILES(new DefaultDatabaseFiles()),

    /**
     * Everything is stored in MongoDB
     * (Online Database which stores data in json format)
     */
    MONGODB(new DefaultDatabaseMongoDB()),

    /**
     * Everything is stored in MariaDB
     * (Online Database requires PHPMyAdmin)
     */
    MYSQL(new DefaultDatabaseMysQL());

    /**
     * The {@link IDatabase} for the {@link DatabaseType}
     */
    private final IDatabase database;

    /**
     * Searches for the {@link IDatabase} by the given type
     *
     * @param type the type to search
     * @return database
     */
    public static IDatabase getDatabaseFromType(DatabaseType type) {
        for (DatabaseType value : values()) {
            if (value == type) {
                return value.getDatabase();
            }
        }
        return null;
    }
}
