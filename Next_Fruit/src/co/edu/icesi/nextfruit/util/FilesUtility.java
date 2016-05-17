package co.edu.icesi.nextfruit.util;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JFileChooser;

/**
 * This class helps the GUI to open Open and Save dialogs to make the user select a file or a directory.
 * @author cjortegon
 */
public class FilesUtility {

	public static File[] listFiles(String folder, String extension) {
		return listFiles(new java.io.File(folder), extension);
	}

	/**
	 * This method is used to list all the files from an specific location that have the given extension.
	 * @param folder Parent folder to look up files.
	 * @param extension The filter extension name.
	 * @return The list of files that satisfy the restriction. 
	 */
	public static File[] listFiles(java.io.File folder, String extension) {
		extension = extension.toLowerCase();
		java.io.File files[] = folder.listFiles();
		ArrayList<File> realFiles = new ArrayList<>();
		for (java.io.File file : files) {
			if(file.getName().toLowerCase().endsWith("."+extension))
				realFiles.add(new File(file.getAbsolutePath()));
		}
		if(realFiles.size() > 0) {
			File extensionFiles[] = new File[realFiles.size()];
			for (int i = 0; i < realFiles.size(); i++)
				extensionFiles[i] = realFiles.get(i);
			return extensionFiles;
		}
		return null;
	}

	/**
	 * This method is used to choose a file path.
	 * @param parent If you want to attach it to the opener window. May be null.
	 * @param title For the dialog window.
	 * @return
	 */
	public static java.io.File loadFile(Component parent, String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("./"));
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setVisible(true);
		if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * This method is used to choose a directory path.
	 * @param parent If you want to attach it to the opener window. May be null.
	 * @param title For the dialog window.
	 * @return
	 */
	public static java.io.File loadDirectory(Component parent, String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("./"));
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setVisible(true);
		if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * This method return a File object created by the user using the GUI.
	 * @param parent If you want to attach it to the opener window. May be null.
	 * @param title For the dialog window.
	 * @return File to save.
	 */
	public static java.io.File chooseFileToSave(Component parent, String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("./"));
		chooser.setDialogTitle(title);
		chooser.setVisible(true);
		if(parent == null)
			parent = chooser;
		if(chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

}
