package co.edu.icesi.nextfruit.util;

import java.util.ArrayList;

import javax.swing.JFileChooser;

public class FilesUtility {

	public static File[] listFiles(String folder, String extension) {
		return listFiles(new java.io.File(folder), extension);
	}

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

	public static java.io.File loadFile(String title) {
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

	public static java.io.File loadDirectory(String title) {
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

}
