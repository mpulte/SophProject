package com.discordbot.sql;

import com.discordbot.command.CommandListener;
import com.discordbot.command.CommandSetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandDB extends SQLiteDatabase<CommandSetting, Class<? extends CommandListener>> {

    private static final String DB_FILE = "commands.db";
    private static final int DB_VERSION = 1;

    // table constants
    private static final String COMMAND = "commands";
    private static final String COMMAND_CLASS = "cmd_class";
    private static final String COMMAND_TAG = "cmd_tag";
    private static final String COMMAND_IS_ENABLED = "cmd_is_enabled";

    // create table statement
    private static final String CREATE_TABLE_COMMAND =
            "CREATE TABLE IF NOT EXISTS " + COMMAND + " (" +
            COMMAND_CLASS      + " TEXT     NOT NULL  PRIMARY KEY, " +
            COMMAND_TAG        + " TEXT     NOT NULL, " +
            COMMAND_IS_ENABLED + " INTEGER  NOT NULL);";

    // drop table statement
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + COMMAND;

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(DB_FILE);

    public CommandDB() {
        super(connectionPool, DB_VERSION);
    } // constructor

    @Override
    protected void onCreate() {
        query(CREATE_TABLE_COMMAND);
    } // method onCreate

    @Override
    protected void onReset() {
        onDestroy();
        onCreate();
    } // method onReset

    @Override
    protected void onDestroy() {
        query(DROP_TABLE);
    }  // method onDestroy

    @Override
    protected void onUpgrade(int oldVersion, int newVersion) {
        onReset();
        LOG.info("Upgrading " + DB_FILE + " from version " + oldVersion + " to " + newVersion);
    } // method onReset

    @Override
    public CommandSetting select(Class<? extends CommandListener> cls) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + COMMAND + " WHERE " + COMMAND_CLASS + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, cls.getName());
            resultSet = statement.executeQuery();
            CommandSetting setting = null;
            if (resultSet.next()) {
                setting = new CommandSetting(
                        resultSet.getString(COMMAND_CLASS),
                        resultSet.getString(COMMAND_TAG),
                        resultSet.getInt(COMMAND_IS_ENABLED) == TRUE);
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
    } // method select

    @Override
    public List<CommandSetting> selectAll() {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + COMMAND;

        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            List<CommandSetting> settings = new ArrayList<>();
            while (resultSet.next()) {
                try {
                    settings.add(new CommandSetting(
                            resultSet.getString(COMMAND_CLASS),
                            resultSet.getString(COMMAND_TAG),
                            resultSet.getInt(COMMAND_IS_ENABLED) == TRUE));
                } catch (ClassNotFoundException e) {
                    LOG.log(e);
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
    } // method select

    @Override
    public int insert(CommandSetting...settings) {
        String query = "INSERT INTO " + COMMAND +
                " (" + COMMAND_CLASS + "," + COMMAND_TAG + "," + COMMAND_IS_ENABLED + ")" +
                "VALUES (?,?,?)";

        int result = 0;
        for (CommandSetting setting : settings) {
            query(query,
                    setting.getCls().getName(),
                    setting.getTag(),
                    setting.isEnabled() ? TRUE_STRING : FALSE_STRING);
        }
        return result;
    } // method insert

    @Override
    public int update(CommandSetting...settings) {
        String query = "UPDATE " + COMMAND + " SET " +
                COMMAND_TAG + " = ?, " +
                COMMAND_IS_ENABLED + " = ? " +
                "WHERE " + COMMAND_CLASS + " = ?";

        int result = 0;
        for (CommandSetting setting : settings) {
            query(query,
                    setting.getTag(),
                    setting.isEnabled() ? TRUE_STRING : FALSE_STRING,
                    setting.getCls().getName());
        }
        return result;
    } // method update



    @Override
    public int delete(Class...classes) {
        String query = "DELETE FROM " + COMMAND + " WHERE " + COMMAND_CLASS + " = ?";

        int result = 0;
        for (Class cls : classes) {
            result += query(query, cls.getName());
        }
        return result;
    } // method delete

    @Override
    public boolean exists(Class<? extends CommandListener> cls) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT " + COMMAND_CLASS + " FROM " + COMMAND + " WHERE " + COMMAND_CLASS + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, cls.getName());
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
    } // method exists

} // class DiscordBotDB