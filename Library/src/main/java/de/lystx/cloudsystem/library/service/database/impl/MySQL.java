package de.lystx.cloudsystem.library.service.database.impl;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.database.IDatabase;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.*;

public class MySQL implements IDatabase {

    private final DatabaseService databaseService;

    private MySQLConnection mySQLConnection;

    public MySQL(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void connect() {

        this.mySQLConnection = new MySQLConnection();
        mySQLConnection.setHost(databaseService.getHost());
        mySQLConnection.setUser(databaseService.getUsername());
        mySQLConnection.setPassword(databaseService.getPassword());
        mySQLConnection.setDb(databaseService.getDefaultDatabase());
        mySQLConnection.setPort(databaseService.getPort());

        if (!mySQLConnection.connect()) {
            databaseService.getCloudLibrary().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't connect to §eMySQL Database§c!");
        } else {
            mySQLConnection.createTable(databaseService.getCollectionOrTable(), "uuid VARCHAR(36), jsonData VARCHAR(13489)");
            this.databaseService.getCloudLibrary().getConsole().getLogger().sendMessage("INFO", "§9Connected to §bMySQL Database§9!");
        }
    }

    @Override
    public void disconnect() {
        this.mySQLConnection.close();
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        try {
            PreparedStatement ps = this.mySQLConnection.getCon().prepareStatement("SELECT * FROM " + this.databaseService.getCollectionOrTable() + " WHERE uuid = '" + uuid.toString() + "'");
            ResultSet rs = ps.executeQuery();
            boolean var = rs.next();
            rs.close();
            ps.close();
            return var;
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return this.mySQLConnection.isConnected();
    }


    @Override
    public void registerPlayer(CloudPlayer cloudPlayer) {
        if (!this.isRegistered(cloudPlayer.getUniqueId())) {
            CloudPlayerData data = new DefaultCloudPlayerData(cloudPlayer.getUniqueId(), cloudPlayer.getName(), cloudPlayer.getIpAddress());
            this.setPlayerData(cloudPlayer.getUniqueId(), data);
        } else {
            CloudPlayerData cloudPlayerData = this.getPlayerData(cloudPlayer.getUniqueId());
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUniqueId(), cloudPlayer.getName(), cloudPlayerData.getPermissionEntries(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart(), cloudPlayerData.getFirstLogin(), cloudPlayerData.getLastLogin());
            this.setPlayerData(cloudPlayer.getUniqueId(), newData);
        }
    }

    @Override
    public CloudPlayerData getPlayerData(UUID uuid) {
        String data = null;
        try {
            PreparedStatement ps = this.mySQLConnection.getCon().prepareStatement("SELECT * FROM " + this.databaseService.getCollectionOrTable() + " WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                data = result.getString(2);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Document document = new Document(data);
            return document.getObject(document.getJsonObject(), CloudPlayerData.class);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        try {
            if (this.isRegistered(uuid)) {
                PreparedStatement statement = this.mySQLConnection.getCon().prepareStatement("UPDATE " + this.databaseService.getCollectionOrTable() + " SET jsonData = ? WHERE uuid = '" + uuid.toString() + "';");
                statement.setString(1, new Document().append(data).toString());
                statement.executeUpdate();
                statement.close();
            } else {
                PreparedStatement statement = this.mySQLConnection.getCon().prepareStatement("INSERT INTO " + this.databaseService.getCollectionOrTable() + " (uuid, jsonData) VALUES(?, ?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, new Document().append(data).toString());
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public List<CloudPlayerData> loadEntries() {
        List<CloudPlayerData> list = new LinkedList<>();
        try {
            PreparedStatement statement = this.mySQLConnection.getCon().prepareStatement("SELECT * FROM " + this.databaseService.getCollectionOrTable());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String data = result.getString(2);
                Document document = new Document(data);
                CloudPlayerData cloudPlayerData = document.getAs(CloudPlayerData.class);
                list.add(cloudPlayerData);
            }
        } catch (SQLException e) {}
        return list;
    }

    @Getter
    @Setter
    public static class MySQLConnection {

        private String host, user, password, db;
        private int port = 3306;
        private Connection con;
        private boolean debug = false;


        public boolean connect() {
            try {
                MysqlDataSource dataSource = new MysqlDataSource();
                dataSource.setServerName(host);
                dataSource.setPort(port);
                dataSource.setDatabaseName(db);
                dataSource.setUser(user);
                dataSource.setPassword(password);
                dataSource.setAllowMultiQueries(true);
                con = dataSource.getConnection();
                return true;
            } catch(SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        public boolean isConnected() {
            if (con == null) {
                return false;
            }
            try {
                if (con.isClosed()) {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        public boolean close() {
            try {
                con.close();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }

        public void createTable(String table, String colums) {
            this.custom("CREATE TABLE IF NOT EXISTS " + table + "(" + colums + ")");
        }

        public boolean custom(String sql) {
            Statement stmt = null;
            try {
                stmt = con.createStatement();
                stmt.execute(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    stmt.close();
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            return false;
        }
    }
}
