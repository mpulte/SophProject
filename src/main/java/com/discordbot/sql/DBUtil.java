package com.discordbot.sql;

import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.*;

public class DBUtil {

    private static final SimpleLog LOG = SimpleLog.getLog("SQLite");

    public static void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            LOG.log(e);
        }
    } // method closeStatement

    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            LOG.log(e);
        }
    } // method closePreparedStatement

    public static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            LOG.log(e);
        }
    } // method closeResultSet

} // class DBUtil
