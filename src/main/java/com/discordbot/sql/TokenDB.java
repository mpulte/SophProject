package com.discordbot.sql;

import com.discordbot.model.Token;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TokenDB extends SQLiteDatabase<Token, String> {

    private static final int DB_VERSION = 1;

    // table constants
    private static final String TOKEN = "token";
    private static final String TOKEN_TOKEN = "token";
    private static final String TOKEN_NAME = "name";

    // create table statement
    private static final String CREATE_TABLE_TOKEN =
            "CREATE TABLE IF NOT EXISTS " + TOKEN + " (" +
                    TOKEN_TOKEN + " TEXT  NOT NULL  PRIMARY KEY, " +
                    TOKEN_NAME  + " TEXT  NOT NULL);";

    // drop table statement
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TOKEN;

    public TokenDB() {
        super(DB_VERSION);
    } // constructor

    @Override
    protected void onCreate() {
        query(CREATE_TABLE_TOKEN);
    } // method onCreate

    @Override
    protected void onDestroy() {
        query(DROP_TABLE);
    }  // method onDestroy

    @Override
    protected void onUpgrade(int oldVersion, int newVersion) {
        onReset();
        LOG.info("Upgrading TokenDB from version " + oldVersion + " to " + newVersion);
    } // method onReset

    @Override
    public Token select(String token) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + TOKEN + " WHERE " + TOKEN_TOKEN + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, token);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Token(resultSet.getString(TOKEN_TOKEN), resultSet.getString(TOKEN_NAME));
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
    public List<Token> selectAll() {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT * FROM " + TOKEN;

        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            List<Token> tokens = new ArrayList<>();
            while (resultSet.next()) {
                tokens.add(new Token(resultSet.getString(TOKEN_TOKEN), resultSet.getString(TOKEN_NAME)));
            }
            return tokens;
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
    public int insert(Token...tokens) {
        String query = "INSERT INTO " + TOKEN +
                " (" + TOKEN_TOKEN + "," + TOKEN_NAME + ")" +
                "VALUES (?,?)";

        int result = 0;
        for (Token token : tokens) {
            query(query, token.getToken(), token.getName());
        }
        return result;
    } // method insert

    @Override
    public int update(Token...tokens) {
        String query = "UPDATE " + TOKEN + " SET " + TOKEN_NAME + " = ?" + " WHERE " + TOKEN_TOKEN + " = ?";

        int result = 0;
        for (Token token : tokens) {
            query(query, token.getName(), token.getToken());
        }
        return result;
    } // method update



    @Override
    public int delete(String...tokens) {
        String query = "DELETE FROM " + TOKEN + " WHERE " + TOKEN_TOKEN + " = ?";

        int result = 0;
        for (String token : tokens) {
            result += query(query, token);
        }
        return result;
    } // method delete

    @Override
    public boolean exists(String token) {
        Connection connection = connectionPool.getConnection();
        if (connection == null) {
            return false;
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String query = "SELECT " + TOKEN_TOKEN + " FROM " + TOKEN + " WHERE " + TOKEN_TOKEN + " = ?";

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, token);
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
}
