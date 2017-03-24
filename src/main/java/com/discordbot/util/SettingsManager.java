package com.discordbot.util;

import com.discordbot.model.Setting;
import com.discordbot.sql.SettingDB;

import java.security.InvalidKeyException;
import java.util.IllegalFormatException;

public final class SettingsManager {

    private static SettingDB database = new SettingDB();

    public static void setString(String key, String value) {
        if (database.exists(key)) {
            database.update(new Setting(key, value));
        } else {
            database.insert(new Setting(key, value));
        }
    }

    public static String getString(String key) throws InvalidKeyException {
        Setting result = database.select(key);
        if (result == null) {
            throw new InvalidKeyException("Setting " + key + " does not exist");
        }
        return result.getValue();
    }

    public static void setDouble(String key, double value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Double.toString(value)));
        } else {
            database.insert(new Setting(key, Double.toString(value)));
        }
    }

    public static double getDouble(String key) throws InvalidKeyException, IllegalFormatException {
        Setting result = database.select(key);
        if (result == null) {
            throw new InvalidKeyException("Setting " + key + " does not exist");
        }
        return Double.parseDouble(result.getValue());
    }

    public static void setInt(String key, int value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Integer.toString(value)));
        } else {
            database.insert(new Setting(key, Integer.toString(value)));
        }
    }

    public static int getInt(String key) throws InvalidKeyException, IllegalFormatException {
        Setting result = database.select(key);
        if (result == null) {
            throw new InvalidKeyException("Setting " + key + " does not exist");
        }
        return Integer.parseInt(result.getValue());
    }

    public static void setBoolean(String key, boolean value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Boolean.toString(value)));
        } else {
            database.insert(new Setting(key, Boolean.toString(value)));
        }
    }

    public static boolean getBoolean(String key) throws InvalidKeyException, IllegalFormatException {
        Setting result = database.select(key);
        if (result == null) {
            throw new InvalidKeyException("Setting " + key + " does not exist");
        }
        return Boolean.parseBoolean(result.getValue());
    }
}
