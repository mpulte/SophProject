package com.discordbot.util;

import java.io.File;

import javax.swing.JFileChooser;

public final class FileUtil {
	
	public static final String OPEN = "Open";
	public static final String SAVE = "Save As";
	public static final String SELECT = "Select File or Folder";
	public static final String SELECT_FILE = "Select File";
	public static final String SELECT_FOLDER = "Select Folder";
	
	
	public static File chooseFile(String title, boolean allFiles, int selectionMode) {
		// set up file chooser
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setDialogTitle(title);
		fileChooser.setFileSelectionMode(selectionMode);
		fileChooser.setAcceptAllFileFilterUsed(allFiles);
		
		// user selected file, return it
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		
		// user canceled dialog
		return null;
	} // method chooseFile
	
	public static File chooseFile(String title, boolean allFiles) {
		return chooseFile(title, allFiles, JFileChooser.FILES_AND_DIRECTORIES);
	} // method chooseFile
	
	public static File chooseFile(String title, int selectionMode) {
		return chooseFile(title, false, selectionMode);
	} // method chooseFile
	
	public static File chooseFile(String title) {
		return chooseFile(title, true, JFileChooser.FILES_AND_DIRECTORIES);
	} // method chooseFile

} // class FileUtil