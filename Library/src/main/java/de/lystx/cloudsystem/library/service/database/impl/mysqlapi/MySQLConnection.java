package de.lystx.cloudsystem.library.service.database.impl.mysqlapi;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.lystx.cloudsystem.library.service.database.impl.mysqlapi.utils.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;

@Getter @Setter
public class MySQLConnection {

    private String host, user, password, db;
    private int port = 3306;
    private Connection con;
    private boolean debug = false;

    public MySQLConnection() {}

    public MySQLConnection(String host) {
        this.host = host;
    }

    public MySQLConnection(String host, String user, String pw, String db) {
        this.host = host;
        this.user = user;
        this.password = pw;
        this.db = db;
    }


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

    public boolean tableInsert(String table, String columns, String... data) {
        String sqldata = "";
        int i = 0;
        for (String d : data) {
            sqldata = sqldata + "'" + d + "'";
            i++;
            if(i != data.length) {
                sqldata = sqldata + ", ";
            }
        }


        String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + sqldata + ");";
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean rowUpdate(String table, UpdateValue value, String filter) {
        String change = "";
        int i = 0;
        for(String key : value.getKeys()) {
            change = change + key + " = '" + value.get(key) + "'";
            i++;
            if(i != value.getKeys().size()) {
                change = change + ", ";
            }
        }
        String sql = "UPDATE " + table + " SET " + change + " WHERE " + filter + ";";
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    public Result rowSelect(String table, String columns, String filter) {
        if(columns == null || columns.equals("")) {
            columns = "*";
        }
        String sql = "SELECT " + columns + " FROM " + table;
        if(filter != null && !filter.equals("")) {
            sql = sql + " WHERE " + filter;
        }
        sql = sql + ";";

        Statement stmt;
        ResultSet res;
        try {
            stmt = con.createStatement();
            res = stmt.executeQuery(sql);
            ResultSetMetaData resmeta = res.getMetaData();
            Result result = new Result();
            while(res.next()) {
                Row row = new Row();
                int i = 1;
                boolean bound = true;
                while (bound) {
                    try {
                        row.addcolumn(resmeta.getColumnName(i), res.getObject(i));
                    } catch (SQLException e) {
                        bound = false;
                    }

                    i++;
                }
                result.addrow(row);
            }
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            return new Result();
        }
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
