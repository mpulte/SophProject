package com.discordbot.sql;

import net.dv8tion.jda.core.utils.SimpleLog;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * A connection pool for managing connections to an SQLite database.
 */
public class ConnectionPool {

    private static final SimpleLog LOG = SimpleLog.getLog("SQLite");

    private static Map<String, ConnectionPool> pools = new HashMap<>();

    private SQLiteDataSource dataSource = null;

    /**
     * Private constructor for factory.
     *
     * @param dbFile The path to the SQLite database.
     */
    private ConnectionPool(String dbFile) {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbFile);
    }

    /**
     * Gets the ConnectionPool instance for the given SQLite database file. Creates a new instance if necessary.
     *
     * @param dbFile The path to the SQLite database.
     * @return the ConnectionPool instance.
     */
    public static synchronized ConnectionPool getInstance(String dbFile) {
        if (!pools.containsKey(dbFile)) {
            pools.put(dbFile, new ConnectionPool(dbFile));
        }
        return pools.get(dbFile);
    }

    /**
     * Opens a {@link Connection} to the SQLite database.
     *
     * @return the {@link Connection} to the SQLite database.
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOG.log(e);
            return null;
        }
    }

    /**
     * Closes a {@link Connection} to the SQLite database.
     *
     * @param connection The {@link Connection} to close.
     */
    public void freeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.log(e);
        }
    }

}
