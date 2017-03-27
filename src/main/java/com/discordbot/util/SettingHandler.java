package com.discordbot.util;

import com.discordbot.model.Setting;
import com.discordbot.sql.SettingDB;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

/**
 * A handler for getting and setting {@link Setting} in the {@link SettingDB} database.
 */
public final class SettingHandler {

    private static SettingDB database = new SettingDB();

    private static List<ChangeListener<String>> stringListenrers = new ArrayList<>();
    private static List<ChangeListener<Double>> doubleListeners = new ArrayList<>();
    private static List<ChangeListener<Integer>> integerListeners = new ArrayList<>();
    private static List<ChangeListener<Boolean>> booleanListeners = new ArrayList<>();

    /**
     * Sets a {@link Setting}.
     *
     * @param key   The key of the {@link Setting} to set.
     * @param value The value of the {@link Setting} to set.
     */
    public static void setString(String key, String value) {
        if (database.exists(key)) {
            database.update(new Setting(key, value));
        } else {
            database.insert(new Setting(key, value));
        }
        for (ChangeListener<String> listener : stringListenrers) {
            listener.onChange(key, value);
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
     * @param key   The key of the {@link Setting} to set.
     * @param value The value of the {@link Setting} to set.
     */
    public static void setDouble(String key, double value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Double.toString(value)));
        } else {
            database.insert(new Setting(key, Double.toString(value)));
        }
        for (ChangeListener<Double> listener : doubleListeners) {
            listener.onChange(key, value);
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
     * @param key   The key of the {@link Setting} to set.
     * @param value The value of the {@link Setting} to set.
     */
    public static void setInt(String key, int value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Integer.toString(value)));
        } else {
            database.insert(new Setting(key, Integer.toString(value)));
        }
        for (ChangeListener<Integer> listener : integerListeners) {
            listener.onChange(key, value);
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
     * @param key   The key of the {@link Setting} to set.
     * @param value The value of the {@link Setting} to set.
     */
    public static void setBoolean(String key, boolean value) {
        if (database.exists(key)) {
            database.update(new Setting(key, Boolean.toString(value)));
        } else {
            database.insert(new Setting(key, Boolean.toString(value)));
        }
        for (ChangeListener<Boolean> listener : booleanListeners) {
            listener.onChange(key, value);
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

    /**
     * Adds a {@link ChangeListener<String>}.
     *
     * @param listener the {@link ChangeListener<String>} to add.
     */
    public static void addStringChangeListener(ChangeListener<String> listener) {
        stringListenrers.add(listener);
    }

    /**
     * Adds a {@link ChangeListener<Double>}.
     *
     * @param listener the {@link ChangeListener<Double>} to add.
     */
    public static void addDoubleChangeListener(ChangeListener<Double> listener) {
        doubleListeners.add(listener);
    }

    /**
     * Adds a {@link ChangeListener<Integer>}.
     *
     * @param listener the {@link ChangeListener<Integer>} to add.
     */
    public static void addIntegerChangeListener(ChangeListener<Integer> listener) {
        integerListeners.add(listener);
    }

    /**
     * Adds a {@link ChangeListener<Boolean>}.
     *
     * @param listener the {@link ChangeListener<Boolean>} to add.
     */
    public static void addBooleanChangeListener(ChangeListener<Boolean> listener) {
        booleanListeners.add(listener);
    }

    /**
     * Removes a {@link ChangeListener<String>}.
     *
     * @param listener the {@link ChangeListener<String>} to remove.
     */
    public static void removeStringChangeListener(ChangeListener<String> listener) {
        stringListenrers.remove(listener);
    }

    /**
     * Removes a {@link ChangeListener<Double>}.
     *
     * @param listener the {@link ChangeListener<Double>} to remove.
     */
    public static void removeDoubleChangeListener(ChangeListener<Double> listener) {
        doubleListeners.remove(listener);
    }

    /**
     * Removes a {@link ChangeListener<Integer>}.
     *
     * @param listener the {@link ChangeListener<Integer>} to remove.
     */
    public static void removeIntegerChangeListener(ChangeListener<Integer> listener) {
        integerListeners.remove(listener);
    }

    /**
     * Removes a {@link ChangeListener<Boolean>}.
     *
     * @param listener the {@link ChangeListener<Boolean>} to remove.
     */
    public static void removeBooleanChangeListener(ChangeListener<Boolean> listener) {
        booleanListeners.remove(listener);
    }

    /**
     * Handles changes to settings.
     */
    public interface ChangeListener<T> {
        /**
         * Called when changes are made to the {@link Setting} of type {@link T}.
         *
         * @param key   The key of the {@link Setting}.
         * @param value The value of the {@link Setting}.
         */
        void onChange(String key, T value);
    }

}
