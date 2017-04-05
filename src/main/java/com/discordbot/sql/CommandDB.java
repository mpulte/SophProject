package com.discordbot.sql;

import com.discordbot.command.CommandSetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link SQLiteDatabase} for querying the Command Database.
 *
 * @see SQLiteDatabase
 */
public class CommandDB extends SQLiteDatabase<CommandSetting, String> {

    private static final int DB_VERSION = 1;

    // table constants
    private static final String COMMAND = "commands";
    private static final String COMMAND_CLASS = "cmd_class";
    private static final String COMMAND_TAG = "cmd_tag";
    private static final String COMMAND_IS_ENABLED = "cmd_is_enabled";

    // create table statement
    private static final String CREATE_TABLE_COMMAND =
            "CREATE TABLE IF NOT EXISTS " + COMMAND + " (" +
                    COMMAND_CLASS + " TEXT     NOT NULL  PRIMARY KEY, " +
                    COMMAND_TAG + " TEXT     NOT NULL, " +
                    COMMAND_IS_ENABLED + " INTEGER  NOT NULL);";

    // drop table statement
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + COMMAND;

    /**
     * Default constructor
     */
    public CommandDB() {
        super(DB_VERSION);
    }

    /**
     * Called by {@link SQLiteDatabase} if the database needs to be created.
     */
    @Override
    protected void onCreate() {
        query(CREATE_TABLE_COMMAND);
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
     * @param oldVersion The previous version of the CommandDB.
     * @param newVersion The new version of the CommandDB.
     */
    @Override
    protected void onUpgrade(int oldVersion, int newVersion) {
        onReset();
        LOG.info("Upgrading CommandDB from version " + oldVersion + " to " + newVersion);
    }

    /**
     * Selects a {@link CommandSetting}.
     *
     * @param className The {@link Class} of the {@link CommandSetting} to select.
     * @return the {@link CommandSetting} or null if no such {@link CommandSetting} exists.
     */
    @Override
    public CommandSetting select(String className) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + COMMAND + " WHERE " + COMMAND_CLASS + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, className);
            resultSet = statement.executeQuery();
            CommandSetting setting = null;
            if (resultSet.next()) {
                setting = new CommandSetting(
                        resultSet.getString(COMMAND_CLASS),
                        resultSet.getString(COMMAND_TAG),
                        Boolean.parseBoolean(resultSet.getString(COMMAND_IS_ENABLED)));
            }
            return setting;
        } catch (SQLException | ClassNotFoundException e) {
            LOG.log(e);
            return null;
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(statement);
            connectionPool.freeConnection(connection);
        }
    }

    /**
     * Selects all {@link CommandSetting}s.
     *
     * @return a {@link List<CommandSetting>}.
     */
    @Override
    public List<CommandSetting> selectAll() {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + COMMAND + " ORDER BY " + COMMAND_CLASS;

        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            List<CommandSetting> settings = new ArrayList<>();
            while (resultSet.next()) {
                try {
                    settings.add(new CommandSetting(
                            resultSet.getString(COMMAND_CLASS),
                            resultSet.getString(COMMAND_TAG),
                            Boolean.parseBoolean(resultSet.getString(COMMAND_IS_ENABLED))));
                } catch (ClassNotFoundException e) {
                    // class not found means that a class was renamed, moved, or deleted
                    // it should be addressed when the application is initialized, use selectAllKeys to get the key
                    LOG.warn(e.getMessage());
                }
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
     * Selects all class names.
     *
     * @return a {@link List<String>} of class names.
     */
    public List<String> selectAllKeys() {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT " + COMMAND_CLASS + " FROM " + COMMAND + " ORDER BY " + COMMAND_CLASS;

        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            List<String> tags = new ArrayList<>();
            while (resultSet.next()) {
                tags.add(resultSet.getString(1));
            }
            return tags;
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
     * Inserts one or more {@link CommandSetting}.
     *
     * @param settings The {@link CommandSetting}s to insert.
     * @return the number of {@link CommandSetting}s inserted.
     */
    @Override
    public int insert(CommandSetting... settings) {
        String query = "INSERT INTO " + COMMAND +
                " (" + COMMAND_CLASS + "," + COMMAND_TAG + "," + COMMAND_IS_ENABLED + ")" +
                "VALUES (?,?,?)";

        int result = 0;
        for (CommandSetting setting : settings) {
            result += query(query,
                    setting.getCls().getName(),
                    setting.getTag(),
                    Boolean.toString(setting.isEnabled()));
        }
        return result;
    }

    /**
     * Updates one or more {@link CommandSetting}.
     *
     * @param settings The {@link CommandSetting}s to update.
     * @return the number of {@link CommandSetting}s updated.
     */
    @Override
    public int update(CommandSetting... settings) {
        String query = "UPDATE " + COMMAND + " SET " +
                COMMAND_TAG + " = ?, " +
                COMMAND_IS_ENABLED + " = ? " +
                "WHERE " + COMMAND_CLASS + " = ?";

        int result = 0;
        for (CommandSetting setting : settings) {
            result += query(query,
                    setting.getTag(),
                    Boolean.toString(setting.isEnabled()),
                    setting.getCls().getName());
        }
        return result;
    }

    /**
     * Deletes one or more {@link CommandSetting}.
     *
     * @param classNames The class names of the {@link CommandSetting}s to delete.
     * @return the number of {@link CommandSetting}s deleted.
     */
    @Override
    public int delete(String... classNames) {
        String query = "DELETE FROM " + COMMAND + " WHERE " + COMMAND_CLASS + " = ?";

        int result = 0;
        for (String className : classNames) {
            result += query(query, className);
        }
        return result;
    }

    /**
     * Checks if a {@link CommandSetting} of a given {@link Class} exists.
     *
     * @param className The class name of the {@link CommandSetting}.
     * @return <tt>true</tt> if a {@link CommandSetting} exists, <tt>false</tt> otherwise.
     */
    @Override
    public boolean exists(String className) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT " + COMMAND_CLASS + " FROM " + COMMAND + " WHERE " + COMMAND_CLASS + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, className);
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