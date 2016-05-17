package co.edu.icesi.nextfruit.util;

/**
 * File wrapper to display only the name of the file in the toString()
 * @author cjortegon
 */
public class File extends java.io.File {

	public File(String pathname) {
		super(pathname);
	}

	@Override
	public String toString() {
		return getName();
	}

}
