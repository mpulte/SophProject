package com.discordbot.sql;

import com.discordbot.model.Setting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link SQLiteDatabase} for querying the Settings Database.
 *
 * @see SQLiteDatabase
 */
public class SettingDB extends SQLiteDatabase<Setting, String> {

    protected static final int DB_VERSION = 1;

    // table constants
    protected final static String SETTING = "setting";
    protected final static String SETTING_KEY = "set_key";
    protected final static String SETTING_VALUE = "set_value";

    // create table statement
    private final static String CREATE_TABLE_SETTING =
            "CREATE TABLE IF NOT EXISTS " + SETTING + " (" +
                    SETTING_KEY +   " TEXT     NOT NULL  PRIMARY KEY, " +
                    SETTING_VALUE + " TEXT     NOT NULL);";

    // drop table statement
    private final static String DROP_TABLE = "DROP TABLE IF EXISTS " + SETTING;

    private final String TABLE;

    /**
     * Default constructor
     */
    public SettingDB() {
        super(DB_VERSION);
        TABLE = SETTING;
    }

    /**
     * Protected constructor to be used by subclasses
     *
     * @param table_name The name to use for the table.
     */
    protected SettingDB(String table_name) {
        super(DB_VERSION);

        // reserve setting table name for this class only
        if (table_name.equals("setting")) {
            throw new IllegalArgumentException("Table name cannot be setting");
        }

        TABLE = table_name;
    }

    /**
     * Called by {@link SQLiteDatabase} if the database needs to be created.
     */
    @Override
    protected void onCreate() {
        query(CREATE_TABLE_SETTING);
    }

    /**
     * Called by {@link SQLiteDatabase} if the database needs to be destroyed.
     */
    @Override
    protected void onDestroy() {
        query(DROP_TABLE);
    }

    /**
     * Called by {@link SQLiteDatabase} if the database version has increased.
     *
     * @param oldVersion The previous version of the SettingDB.
     * @param newVersion The new version of the SettingDB.
     */
    @Override
    protected void onUpgrade(int oldVersion, int newVersion) {
        onReset();
        LOG.info("Upgrading SettingDB from version " + oldVersion + " to " + newVersion);
    }

    /**
     * Selects a {@link Setting}.
     *
     * @param key The key of the {@link Setting} to select.
     * @return the {@link Setting} or null if no such {@link Setting} exists.
     */
    @Override
    public Setting select(String key) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + TABLE + " WHERE " + SETTING_KEY + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, key);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Setting(resultSet.getString(SETTING_KEY), resultSet.getString(SETTING_VALUE));
            }
            return null;
        } catch (SQLException e) {
            LOG.log(e);
            return null;
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(statement);
            connectionPool.freeConnection(connection);
        }
    }

    /**
     * Selects all {@link Setting}s.
     *
     * @return a {@link List<Setting>}.
     */
    @Override
    public List<Setting> selectAll() {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + TABLE + " ORDER BY " + SETTING_KEY;

        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            List<Setting> settings = new ArrayList<>();
            while (resultSet.next()) {
                settings.add(new Setting(resultSet.getString(SETTING_KEY), resultSet.getString(SETTING_VALUE)));
            }
            return settings;
        } catch (SQLException e) {
            LOG.log(e);
            return null;
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(statement);
            connectionPool.freeConnection(connection);
        }
    }

    /**
     * Inserts one or more {@link Setting}.
     *
     * @param settings The {@link Setting}s to insert.
     * @return the number of {@link Setting}s inserted.
     */
    @Override
    public int insert(Setting... settings) {
        String query = "INSERT INTO " + TABLE +
                " (" + SETTING_KEY + "," + SETTING_VALUE + ")" +
                "VALUES (?,?)";

        int result = 0;
        for (Setting setting : settings) {
            result += query(query, setting.getKey(), setting.getValue());
        }
        return result;
    }

    /**
     * Updates one or more {@link Setting}.
     *
     * @param settings The {@link Setting}s to update.
     * @return the number of {@link Setting}s updated.
     */
    @Override
    public int update(Setting... settings) {
        String query = "UPDATE " + TABLE + " SET " + SETTING_VALUE + " = ?" + " WHERE " + SETTING_KEY + " = ?";

        int result = 0;
        for (Setting setting : settings) {
            result += query(query, setting.getValue(), setting.getKey());
        }
        return result;
    }

    /**
     * Deletes one or more {@link Setting}.
     *
     * @param keys The key of the {@link Setting}s to delete.
     * @return the number of {@link Setting}s deleted.
     */
    @Override
    public int delete(String... keys) {
        String query = "DELETE FROM " + TABLE + " WHERE " + SETTING_KEY + " = ?";

        int result = 0;
        for (String key : keys) {
            result += query(query, key);
        }
        return result;
    }

    /**
     * Checks if a {@link Setting} of a given key exists.
     *
     * @param key The key of the {@link Setting}.
     * @return <tt>true</tt> if a {@link Setting} exists, <tt>false</tt> otherwise.
     */
    @Override
    public boolean exists(String key) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT " + SETTING_KEY + " FROM " + TABLE + " WHERE " + SETTING_KEY + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, key);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            LOG.log(e);
            return false;
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(statement);
            connectionPool.freeConnection(connection);
        }
    }

}
