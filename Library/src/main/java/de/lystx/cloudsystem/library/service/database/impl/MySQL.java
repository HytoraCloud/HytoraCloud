package de.lystx.cloudsystem.library.service.database.impl;

import de.lystx.cloudsystem.library.service.database.CloudDatabase;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import de.lystx.cloudsystem.library.service.player.impl.DefaultCloudPlayerData;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class MySQL implements CloudDatabase {

    private final DatabaseService databaseService;

    private Connection connection;

    public MySQL(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void connect() {
        String url = "jdbc:mysql://localhost:" + databaseService.getPort() + "/" + databaseService.getDefaultDatabase();
        try (Connection connection = DriverManager.getConnection(url, databaseService.getUsername(), databaseService.getPassword())) {
            this.connection = connection;
            try {
                PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + databaseService.getCollectionOrTable() + "(id INT AUTO_INCREMENT PRIMARY KEY NOT NULL, " +
                        "name VARCHAR(64), " +
                        "uuid VARCHAR(64), " +
                        "permissionEntries ANY(64), " +
                        "permissions VARCHAR(21844), " +
                        "ipAddress VARCHAR(64)" +
                        "notifyServerStart BOOL, " +
                        "firstLogin LONG, " +
                        "lastLogin LONG)");
                ps.executeUpdate();
                ps.close();
            } catch (SQLException exception) {}
        } catch (SQLException e) {
            databaseService.getCloudLibrary().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't connect to §eMySQL Database§c!");
        }

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void registerPlayer(CloudPlayer cloudPlayer) {
        if (!this.isRegistered(cloudPlayer.getUuid())) {
            CloudPlayerData data = new DefaultCloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName());
            this.setPlayerData(cloudPlayer.getUuid(), data);
        } else {
            CloudPlayerData cloudPlayerData = this.getPlayerData(cloudPlayer.getUuid());
            CloudPlayerData newData = new CloudPlayerData(cloudPlayer.getUuid(), cloudPlayer.getName(), cloudPlayerData.getPermissionEntries(), cloudPlayerData.getPermissions(), cloudPlayer.getIpAddress(), cloudPlayerData.isNotifyServerStart(), cloudPlayerData.getFirstLogin(), cloudPlayerData.getLastLogin());
            this.setPlayerData(cloudPlayer.getUuid(), newData);
        }
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM " + databaseService.getCollectionOrTable() + " WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            boolean var = rs.next();
            rs.close();
            ps.close();
            return var;
        } catch (SQLException | NullPointerException exception) {
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return !this.connection.isClosed();
        } catch (SQLException throwables) {
            return false;
        }
    }

    @Override
    public CloudPlayerData getPlayerData(UUID uuid) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM " + this.databaseService.getCollectionOrTable() + " WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.getResultSet();

            CloudPlayerData cloudPlayerData = new CloudPlayerData(
                    uuid,
                    rs.getString("name"),
                    rs.getObject("permissionEntries", ArrayList.class),
                    this.fromMySQLString(rs.getString("permissions")),
                    rs.getString("ipAddress"),
                    rs.getBoolean("notifyServerStart"),
                    rs.getLong("firstLogin"),
                    rs.getLong("lastLogin")
            );
            rs.close();
            ps.close();
            return cloudPlayerData;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void setPlayerData(UUID uuid, CloudPlayerData data) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("INSERT INTO " + databaseService.getCollectionOrTable() + "(name,uuid,permissionEntries,permissions,ipAddress,notifyServerStart,firstLogin,lastLogin) VALUES (?,?,?,?,?,?,?)");
            ps.setString(1, data.getName());
            ps.setString(2, data.getUuid().toString());
            ps.setObject(3, data.getPermissionEntries());
            ps.setString(4, this.toMySQLString(data.getPermissions()));
            ps.setString(5, data.getIpAddress());
            ps.setBoolean(6, data.isNotifyServerStart());
            ps.setLong(7, data.getFirstLogin());
            ps.setLong(8, data.getLastLogin());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException exception) {}
    }

    public void update(UUID uuid, String key, Object value) {
        try {
            PreparedStatement ps = this.connection.prepareStatement("UPDATE " + databaseService.getCollectionOrTable() + " SET " + key + " = ? WHERE uuid = ?");
            ps.setObject(1, value);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException exception) { }
    }


    /**
     * INTERNAL
     */



    public List<String> fromMySQLString(String input) {
        return new LinkedList<>(Arrays.asList(input.split(",")));
    }

    public String toMySQLString(List<String> input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : input) {
            stringBuilder.append(s).append(",");
        }
        return stringBuilder.toString();
    }

}
