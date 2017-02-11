package com.discordbot.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.dv8tion.jda.core.utils.SimpleLog;

public class SettingsHandler {
	
	private static final String FILE_NAME = "settings.xml";
	private static final SimpleLog LOG = SimpleLog.getLog("Settings");
	
	private static Properties settings = load();
	
	public static void put(String key, String value) {
		settings.setProperty(key, value);
	} // method put
	
	@SafeVarargs
	public static void put(Entry<String, String>...entries) {
		for (Entry<String, String> entry : entries) {
			settings.setProperty(entry.getKey(), entry.getValue());
		}
	} // method put
	
	public static String get(String key) {
		return settings.getProperty(key);
	} // method get
	
	public static Map<String, String> get(String...keys) {
		Map<String, String> settings = new HashMap<>();
		for (String key : keys) {
			// includes null values in map
			settings.put(key, SettingsHandler.settings.getProperty(key));
		}
		return settings;
	} // method get
	
	public static void save() throws IOException {
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(FILE_NAME);
			settings.storeToXML(fileOut, "");
			fileOut.close();
		} catch (IOException e) {
			// try to close just in case
			try {
				if (fileOut != null) {
					fileOut.close();
				}
			} catch (IOException ignore) {}
			// throw the exception to indicate failure to save
			throw e;
		}
	} // method save
	
	private static Properties load() {
		Properties settings = new Properties();
		FileInputStream fileIn = null;
		try {
			fileIn = new FileInputStream(FILE_NAME);
			settings.load(fileIn);
		} catch (IOException e) {
			// unable to open file
			LOG.warn("Unable to load settings from " + FILE_NAME);
		} finally {
			// close the file if it was opened
			if (fileIn != null) {
				try {
					fileIn.close();
				} catch (IOException ignore) {}
			}
		}
		return new Properties();
	} // method 
	
} // class SettingsHandler
