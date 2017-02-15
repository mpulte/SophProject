package com.discordbot.sql;

import net.dv8tion.jda.core.utils.SimpleLog;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionPool {

    private static final SimpleLog LOG = SimpleLog.getLog("SQLite");

    private static Map<String, ConnectionPool> pools = new HashMap<>();

    private SQLiteDataSource dataSource = null;

    private ConnectionPool(String dbFile) {
        dataSource =  new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbFile);
    } // constructor

    public static synchronized ConnectionPool getInstance(String dbFile) {
        if (!pools.containsKey(dbFile)) {
            pools.put(dbFile, new ConnectionPool(dbFile));
        }
        return pools.get(dbFile);
    } // method getInstance

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOG.log(e);
            return null;
        }
    } // method getConnection

    public void freeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.log(e);
        }
    } // method freeConnection

} // class ConnectionPool
