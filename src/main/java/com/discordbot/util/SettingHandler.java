package com.discordbot.util;

import com.discordbot.model.Setting;
import com.discordbot.sql.SettingDB;

import java.security.InvalidKeyException;
import java.util.IllegalFormatException;

/**
 * A handler for getting and setting {@link Setting} in the {@link SettingDB} database.
 */
public final class SettingHandler {

    private static SettingDB database = new SettingDB();

    /**
     * Sets a {@link Setting}.
     *
     * @param key The key of the {@link Setting} to set.
     * @param value The value of the {@link Setting} to set.
     */
    public static void setString(String key, String value) {
        if (database.exists(key)) {
            database.update(new Setting(key, value));
        } else {
            database.insert(new Setting(key, value));
        }
    }

    /**
     * Gets a {@link Setting} as a {@link String}.
     *
     * @param key The key of the {@link Setting} to get.
     * @throws InvalidKeyException if the key does not exist.
     */
    public static String getString(String key) throws InvalidKeyException {
        Setting result = database.select(key);
        if (result == null) {
            throw new InvalidKeyException("Setting " + key + " does not exist");
        }
        return result.getValue();
    }

    /**
     * Sets a {@link Setting}.
     *
     * @param key The key of the {@link Setting} to set.
     * @param value The value of the {@link Setting} to set.
     */
    public static void setDouble(String key, double value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Double.toString(value)));
        } else {
            database.insert(new Setting(key, Double.toString(value)));
        }
    }

    /**
     * Gets a {@link Setting} as a double.
     *
     * @param key The key of the {@link Setting} to get.
     * @throws InvalidKeyException if the key does not exist.
     */
    public static double getDouble(String key) throws InvalidKeyException, IllegalFormatException {
        Setting result = database.select(key);
        if (result == null) {
            throw new InvalidKeyException("Setting " + key + " does not exist");
        }
        return Double.parseDouble(result.getValue());
    }

    /**
     * Sets a {@link Setting}.
     *
     * @param key The key of the {@link Setting} to set.
     * @param value The value of the {@link Setting} to set.
     */
    public static void setInt(String key, int value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Integer.toString(value)));
        } else {
            database.insert(new Setting(key, Integer.toString(value)));
        }
    }

    /**
     * Gets a {@link Setting} as an integer.
     *
     * @param key The key of the {@link Setting} to get.
     * @throws InvalidKeyException if the key does not exist.
     */
    public static int getInt(String key) throws InvalidKeyException, IllegalFormatException {
        Setting result = database.select(key);
        if (result == null) {
            throw new InvalidKeyException("Setting " + key + " does not exist");
        }
        return Integer.parseInt(result.getValue());
    }

    /**
     * Sets a {@link Setting}.
     *
     * @param key The key of the {@link Setting} to set.
     * @param value The value of the {@link Setting} to set.
     */
    public static void setBoolean(String key, boolean value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Boolean.toString(value)));
        } else {
            database.insert(new Setting(key, Boolean.toString(value)));
        }
    }

    /**
     * Gets a {@link Setting} as a boolean.
     *
     * @param key The key of the {@link Setting} to get.
     * @throws InvalidKeyException if the key does not exist.
     */
    public static boolean getBoolean(String key) throws InvalidKeyException, IllegalFormatException {
        Setting result = database.select(key);
        if (result == null) {
            throw new InvalidKeyException("Setting " + key + " does not exist");
        }
        return Boolean.parseBoolean(result.getValue());
    }

}
