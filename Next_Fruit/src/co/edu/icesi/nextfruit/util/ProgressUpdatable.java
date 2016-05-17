package co.edu.icesi.nextfruit.util;

/**
 * Interface to be implemented to keep track of the progress of long execution methods.
 * @author cjortegon
 */
public interface ProgressUpdatable {

	public void updateProgress(double percent);
	public void updateMessage(String message);

}
