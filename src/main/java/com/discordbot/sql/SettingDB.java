package com.discordbot.sql;

import com.discordbot.model.Setting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SettingDB extends SQLiteDatabase<Setting, String> {

    private static final int DB_VERSION = 1;

    // table constants
    private static final String SETTING = "settings";
    private static final String SETTING_KEY = "set_key";
    private static final String SETTING_VALUE = "set_value";

    // create table statement
    private static final String CREATE_TABLE_SETTING =
            "CREATE TABLE IF NOT EXISTS " + SETTING + " (" +
                    SETTING_KEY   + " TEXT     NOT NULL  PRIMARY KEY, " +
                    SETTING_VALUE + " TEXT     NOT NULL);";

    // drop table statement
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + SETTING;

    public SettingDB() {
        super(DB_VERSION);
    } // constructor

    @Override
    protected void onCreate() {
        query(CREATE_TABLE_SETTING);
    } // method onCreate

    @Override
    protected void onDestroy() {
        query(DROP_TABLE);
    }  // method onDestroy

    @Override
    protected void onUpgrade(int oldVersion, int newVersion) {
        onReset();
        LOG.info("Upgrading SettingDB from version " + oldVersion + " to " + newVersion);
    } // method onReset

    @Override
    public Setting select(String key) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + SETTING + " WHERE " + SETTING_KEY + " = ?";

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
    } // method select

    @Override
    public List<Setting> selectAll() {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + SETTING;

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
    } // method select

    @Override
    public int insert(Setting...settings) {
        String query = "INSERT INTO " + SETTING +
                " (" + SETTING_KEY + "," + SETTING_VALUE + ")" +
                "VALUES (?,?)";

        int result = 0;
        for (Setting setting : settings) {
            result += query(query, setting.getKey(), setting.getValue());
        }
        return result;
    } // method insert

    @Override
    public int update(Setting...settings) {
        String query = "UPDATE " + SETTING + " SET " + SETTING_VALUE + " = ?" + " WHERE " + SETTING_KEY + " = ?";

        int result = 0;
        for (Setting setting : settings) {
            result += query(query, setting.getValue(), setting.getKey());
        }
        return result;
    } // method update

    @Override
    public int delete(String...keys) {
        String query = "DELETE FROM " + SETTING + " WHERE " + SETTING_KEY + " = ?";

        int result = 0;
        for (String key : keys) {
            result += query(query, key);
        }
        return result;
    } // method delete

    @Override
    public boolean exists(String key) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT " + SETTING_KEY + " FROM " + SETTING + " WHERE " + SETTING_KEY + " = ?";

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
    } // method exists

} // class DiscordBotDB
