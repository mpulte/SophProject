package com.discordbot.sql;

import net.dv8tion.jda.core.utils.SimpleLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A utility class for databases.
 */
public final class DBUtil {

    private static final SimpleLog LOG = SimpleLog.getLog("SQLite");

    /**
     * Closes the provided {@link Statement}
     *
     * @param statement The {@link Statement} to close.
     */
    public static void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            LOG.log(e);
        }
    }

    /**
     * Closes the provided {@link PreparedStatement}
     *
     * @param preparedStatement The {@link PreparedStatement} to close.
     */
    public static void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            LOG.log(e);
        }
    }

    /**
     * Closes the provided {@link ResultSet}
     *
     * @param resultSet The {@link ResultSet} to close.
     */
    public static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            LOG.log(e);
        }
    }

}
