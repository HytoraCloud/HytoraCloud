package de.lystx.hytoracloud.driver.service.database;

public interface IDatabaseManager {

    /**
     * Gets the {@link IDatabase} the manager works with
     *
     * @return database
     */
    IDatabase getDatabase();

    /**
     * Loads the database and connects
     * @param host the host to connect to
     * @param port the port to connect to
     * @param username the name of the user
     * @param password the password of the user
     * @param collectionOrTable the collection or table (MySQL or MongoDB)
     * @param defaultDatabase the default database
     * @param type the type of the database
     */
    void load(String host, int port, String username, String password, String collectionOrTable, String defaultDatabase, DatabaseType type);

    /**
     * Gets the default database to connect to
     *
     * @return string database
     */
    String getDefaultDatabase();

    /**
     * Gets the default host to connect to
     *
     * @return string host
     */
    String getHost();

    /**
     * Gets the default username to connect with
     *
     * @return string username
     */
    String getUsername();

    /**
     * Gets the default password to connect with
     *
     * @return string password
     */
    String getPassword();

    /**
     * Gets the default port to connect to
     *
     * @return int port
     */
    int getPort();

    /**
     * Gets the default collection or table to connect to
     *
     * @return string collection or table
     */
    String getCollectionOrTable();

    /**
     * Sets the type of the database
     *
     * @param databaseType the type
     */
    void setDatabase(DatabaseType databaseType);
}
