package co.edu.icesi.nextfruit.util;

public class File extends java.io.File {

	public File(String pathname) {
		super(pathname);
	}

	@Override
	public String toString() {
		return getName();
	}

}
