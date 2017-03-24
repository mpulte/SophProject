package com.discordbot.sql;

import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.*;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class SQLiteDatabase<Relation, Key> {

    private static final String DB_FILE = "bot.db";

    protected static final SimpleLog LOG = SimpleLog.getLog("SQLite");

    protected static final int TRUE = 1;
    protected static final int FALSE = 0;
    protected static final String TRUE_STRING = Integer.toString(TRUE);
    protected static final String FALSE_STRING = Integer.toString(FALSE);

    // table constants
    private static final String INFO = "info";
    private static final String INFO_KEY = "info_key";
    private static final String INFO_VALUE = "info_value";

    // table statements
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + INFO + " (" +
            INFO_KEY   + " TEXT     NOT NULL  PRIMARY KEY, " +
            INFO_VALUE + " INTEGER     NOT NULL);";

    protected static final ConnectionPool connectionPool = ConnectionPool.getInstance(DB_FILE);

    protected SQLiteDatabase(int version) {
        if (version < 1) {
            throw new IllegalArgumentException("Database version must be 1 or greater");
        }
        initialize(version);
    } // constructor

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
    } // method initialize

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
    } // method selectVersion

    private int insertVersion(int version) {
       String query = "INSERT INTO " + INFO + " (" + INFO_KEY + "," + INFO_VALUE + ")" +
               "VALUES (?,?)";
       return query(query, getClass().getName(), Integer.toString(version));
    } // method selectVersion

    private int updateVersion(int version) {
        String query = "UPDATE " + INFO + " SET " + INFO_VALUE + " = ? WHERE " + INFO_KEY + " = ?";
        return query(query, Integer.toString(version), getClass().getName());
    } // method selectVersion

    protected abstract void onCreate();
    protected abstract void onDestroy();
    protected abstract void onUpgrade(int oldVersion, int newVersion);

    protected void onReset() {
        onDestroy();
        onCreate();
    } // method onReset

    public abstract Relation select(Key key);
    public abstract List<Relation> selectAll();
    public abstract int insert(Relation...relations);
    public abstract int update(Relation...relations);
    public abstract int delete(Key...keys);
    public abstract boolean exists(Key key);

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
    } // method executeSQL

    protected int query(String query, String...values) {
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
    } // method executeSQL

} // class SQLiteDatabase
