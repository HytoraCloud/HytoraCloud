package de.lystx.hytoracloud.driver.cloudservices.managing.database.impl;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.DatabaseType;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.IDatabase;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.*;

public class DefaultDatabaseMysQL implements IDatabase {

    private MySQLConnection mySQLConnection;


    @Override
    public void connect() {

        this.mySQLConnection = new MySQLConnection();
        mySQLConnection.setHost(CloudDriver.getInstance().getDatabaseManager().getHost());
        mySQLConnection.setUser(CloudDriver.getInstance().getDatabaseManager().getUsername());
        mySQLConnection.setPassword(CloudDriver.getInstance().getDatabaseManager().getPassword());
        mySQLConnection.setDb(CloudDriver.getInstance().getDatabaseManager().getDefaultDatabase());
        mySQLConnection.setPort(CloudDriver.getInstance().getDatabaseManager().getPort());

        if (!mySQLConnection.connect()) {
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't connect to §eMySQL Database§c!");
        } else {
            mySQLConnection.createTable(CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable(), "uuid VARCHAR(36), jsonData VARCHAR(13489)");
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§9Connected to §bMySQL Database§9!");
        }
    }

    @Override
    public void disconnect() {
        this.mySQLConnection.close();
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        try {
            PreparedStatement ps = this.mySQLConnection.getCon().prepareStatement("SELECT * FROM " + CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable() + " WHERE uuid = '" + uuid.toString() + "'");
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
    public void registerPlayer(ICloudPlayer ICloudPlayer) {
        if (!this.isRegistered(ICloudPlayer.getUniqueId())) {
            PlayerInformation data = CloudDriver.getInstance().getPermissionPool().getDefaultPlayerInformation(ICloudPlayer.getUniqueId(), ICloudPlayer.getName(), ICloudPlayer.getIpAddress());
            this.saveOfflinePlayer(ICloudPlayer.getUniqueId(), data);
        } else {
            PlayerInformation playerInformation = this.getOfflinePlayer(ICloudPlayer.getUniqueId());
            PlayerInformation newData = new PlayerInformation(ICloudPlayer.getUniqueId(), ICloudPlayer.getName(), playerInformation.getPermissionEntries(), playerInformation.getExclusivePermissions(), ICloudPlayer.getIpAddress(), playerInformation.isNotifyServerStart(), playerInformation.getFirstLogin(), playerInformation.getLastLogin());
            this.saveOfflinePlayer(ICloudPlayer.getUniqueId(), newData);
        }
    }

    @Override
    public PlayerInformation getOfflinePlayer(UUID uuid) {
        String data = null;
        try {
            PreparedStatement ps = this.mySQLConnection.getCon().prepareStatement("SELECT * FROM " + CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable() + " WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                data = result.getString(2);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            JsonEntity jsonEntity = new JsonEntity(data);
            return jsonEntity.getObject(jsonEntity.getJsonObject(), PlayerInformation.class);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void saveOfflinePlayer(UUID uuid, PlayerInformation data) {
        try {
            if (this.isRegistered(uuid)) {
                PreparedStatement statement = this.mySQLConnection.getCon().prepareStatement("UPDATE " + CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable() + " SET jsonData = ? WHERE uuid = '" + uuid.toString() + "';");
                statement.setString(1, new JsonEntity().append(data).toString());
                statement.executeUpdate();
                statement.close();
            } else {
                PreparedStatement statement = this.mySQLConnection.getCon().prepareStatement("INSERT INTO " + CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable() + " (uuid, jsonData) VALUES(?, ?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, new JsonEntity().append(data).toString());
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public List<PlayerInformation> loadEntries() {
        List<PlayerInformation> list = new LinkedList<>();
        try {
            PreparedStatement statement = this.mySQLConnection.getCon().prepareStatement("SELECT * FROM " + CloudDriver.getInstance().getDatabaseManager().getCollectionOrTable());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String data = result.getString(2);
                JsonEntity jsonEntity = new JsonEntity(data);
                PlayerInformation playerInformation = jsonEntity.getAs(PlayerInformation.class);
                list.add(playerInformation);
            }
        } catch (SQLException e) {}
        return list;
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.MYSQL;
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
