package com.discordbot.sql;

import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.*;
import java.util.List;

/**
 * Contains methods for querying an SQLite database.
 */
@SuppressWarnings("unchecked")
public abstract class SQLiteDatabase<Relation, Key> {

    /**
     * A {@link SimpleLog} to be used by SQLiteDatabase and its subclasses
     */
    protected static final SimpleLog LOG = SimpleLog.getLog("SQLite");

    /**
     * The database file used by SQLiteDatabase
     */
    protected static final String DB_FILE = "bot.db";

    /**
     * The {@link ConnectionPool} to be used by SQLiteDatabase and its subclasses
     */
    protected static final ConnectionPool connectionPool = ConnectionPool.getInstance(DB_FILE);

    // table constants
    private static final String INFO = "info";
    private static final String INFO_KEY = "info_key";
    private static final String INFO_VALUE = "info_value";

    // table statements
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + INFO + " (" +
                    INFO_KEY + " TEXT     NOT NULL  PRIMARY KEY, " +
                    INFO_VALUE + " INTEGER     NOT NULL);";

    /**
     * Protected constructor calls {@link #initialize(int)}.
     *
     * @param version The version of the database. Must be positive.
     */
    protected SQLiteDatabase(int version) {
        if (version < 1) {
            throw new IllegalArgumentException("Database version must be 1 or greater");
        }
        initialize(version);
    }

    /**
     * Initializes the database.
     *
     * @param newVersion The version of the database. Must be positive.
     */
    private void initialize(int newVersion) {
        // create tables if they don't exist
        query(CREATE_TABLE);
        onCreate();

        // update table if necessary
        int oldVersion = selectVersion();
        if (oldVersion == 0) {
            insertVersion(newVersion);
        } else if (newVersion > oldVersion) {
            onUpgrade(oldVersion, newVersion);
            updateVersion(newVersion);
        } else if (newVersion < oldVersion) {
            throw new IllegalArgumentException("New database version must be greater than old version");
        }
    }

    /**
     * Gets the previous version of the database.
     */
    private int selectVersion() {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return 0;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + INFO + " WHERE " + INFO_KEY + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, getClass().getName());
            resultSet = statement.executeQuery();
            int version = 0;
            if (resultSet.next()) {
                version = resultSet.getInt(INFO_VALUE);
            }
            return version;
        } catch (SQLException e) {
            LOG.log(e);
            return 0;
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(statement);
            connectionPool.freeConnection(connection);
        }
    }

    /**
     * Inserts the current version of the database.
     *
     * @param version The version to insert.
     * @return 1 if the version was inserted, 0 if not.
     */
    private int insertVersion(int version) {
        String query = "INSERT INTO " + INFO + " (" + INFO_KEY + "," + INFO_VALUE + ")" +
                "VALUES (?,?)";
        return query(query, getClass().getName(), Integer.toString(version));
    }

    /**
     * Updates the current version of the database.
     *
     * @param version The new version to update.
     * @return 1 if the version was updated, 0 if not.
     */
    private int updateVersion(int version) {
        String query = "UPDATE " + INFO + " SET " + INFO_VALUE + " = ? WHERE " + INFO_KEY + " = ?";
        return query(query, Integer.toString(version), getClass().getName());
    }

    /**
     * Called if the database needs to be created.
     */
    protected abstract void onCreate();

    /**
     * Called if the database needs to be destroyed.
     */
    protected abstract void onDestroy();

    /**
     * Called if the database version has increased.
     *
     * @param oldVersion The previous version of the SettingDB.
     * @param newVersion The new version of the SettingDB.
     */
    protected abstract void onUpgrade(int oldVersion, int newVersion);

    /**
     * Resets the database by calling {@link #onDestroy()} and {@link #onCreate()}.
     */
    protected void onReset() {
        onDestroy();
        onCreate();
    }

    /**
     * Selects a {@link Relation}
     *
     * @param key The {@link Key} of the {@link Relation}
     * @return the {@link Relation} or null if the {@link Key} is not used.
     */
    public abstract Relation select(Key key);

    /**
     * Selects all {@link Relation}s.
     *
     * @return a {@link List<Relation>}.
     */
    public abstract List<Relation> selectAll();

    /**
     * Inserts one or more {@link Relation}.
     *
     * @param relations The {@link Relation}s to insert.
     * @return the number of {@link Relation}s inserted.
     */
    public abstract int insert(Relation... relations);

    /**
     * Updates one or more {@link Relation}.
     *
     * @param relations The {@link Relation}s to update.
     * @return the number of {@link Relation}s updated.
     */
    public abstract int update(Relation... relations);

    /**
     * Deletes one or more {@link Relation}.
     *
     * @param keys The {@link Key} of the {@link Relation}s to delete.
     * @return the number of {@link Relation}s deleted.
     */
    public abstract int delete(Key... keys);

    /**
     * Checks if a {@link Relation} of a given {@link Key} exists.
     *
     * @param key The {@link Key} of the {@link Relation}.
     * @return <tt>true</tt> if a {@link Relation} exists, <tt>false</tt> otherwise.
     */
    public abstract boolean exists(Key key);

    /**
     * Queries the database.
     *
     * @param query The query to perform.
     * @return <tt>true</tt> if the query is successful, <tt>false</tt> otherwise.
     */
    protected boolean query(String query) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return false;
        }

        Statement statement = null;
        try {
            statement = connection.createStatement();
            return statement.execute(query);
        } catch (SQLException e) {
            LOG.log(e);
            return false;
        } finally {
            DBUtil.closeStatement(statement);
            connectionPool.freeConnection(connection);
        }
    }

    /**
     * Queries the database.
     *
     * @param query  The query to perform.
     * @param values The values to prepare the query with.
     * @return the number of {@link Relation}s changed.
     */
    protected int query(String query, String... values) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return 0;
        }

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                statement.setString(i + 1, values[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            LOG.log(e);
            return 0;
        } finally {
            DBUtil.closeStatement(statement);
            connectionPool.freeConnection(connection);
        }
    }

}
