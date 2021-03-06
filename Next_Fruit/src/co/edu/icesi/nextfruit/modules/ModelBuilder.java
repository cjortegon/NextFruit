package co.edu.icesi.nextfruit.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import co.edu.icesi.nextfruit.modules.computervision.FeaturesExtract;
import co.edu.icesi.nextfruit.modules.machinelearning.ClassClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.QualityClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.RipenessClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.SizeClassifier;
import co.edu.icesi.nextfruit.modules.machinelearning.WekaClassifierAdapter;
import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import co.edu.icesi.nextfruit.util.ProgressUpdatable;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

/**
 * This class gets all the features from the images loaded
 */
public class ModelBuilder {

	public static final String NAIVE_BAYES = "Naive Bayes";
	public static final String[] MODEL_TYPES = new String[]{NAIVE_BAYES};

	public static final String QUALITY_CLASSIFIER = "Quality";
	public static final String SIZE_CLASSIFIER = "Size";
	public static final String CLASS_CLASSIFIER = "Class";
	public static final String RIPENESS_CLASSIFIER = "Ripeness";

	private ArrayList<File> images;

	private WekaClassifierAdapter classifiers[];
	private boolean hasLoadedImages, hasLoadedTrainingSet;

	/**
	 * Starts a new classifier class
	 */
	public ModelBuilder() {
		this.classifiers = new WekaClassifierAdapter[4];
	}

	/**
	 * This method reads all the files from the given folder and validates if is a JPEG PNG image.
	 * @param folder Directory This is the location where the fruit images are located.
	 */
	public int loadImages(File folder) {
		File[] files = folder.listFiles();
		images = new ArrayList<>();
		for (File file : files) {
			if(file.isFile() && !file.isHidden()) {
				String name = file.getName().toLowerCase();
				if(name.toLowerCase().endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"))
					images.add(file);
			}
		}
		hasLoadedImages = true;
		System.out.println("Total images to train system: "+images.size());
		return images.size();
	}

	/**
	 * This class process the loaded images to create a training set placed in the WekaClassifier (classifier).
	 * @param classSeparator The special character or expression that separates the tag from the fruit class and the name of the specific instance.
	 * @param destinationFile The destination file where the training set will be saved.
	 * @param calibration The calibration settings.
	 * @param progress May be null. This is the updateable object to keep track of the progress of this method.
	 */
	public void buildDataset(String classSeparator, File destinationFile, CameraCalibration calibration, ProgressUpdatable progress) {

		// Starting classifiers
		this.classifiers[0] = new QualityClassifier(calibration);
		this.classifiers[1] = new SizeClassifier(calibration);
		this.classifiers[2] = new ClassClassifier(calibration);
		this.classifiers[3] = new RipenessClassifier(calibration);

		// Extracting features from images and creating new instances from that
		int count = 0;
		long startTime = System.currentTimeMillis();
		if(progress != null) {
			progress.updateProgress(0);
			progress.updateMessage("Generating trainning set with "+images.size()+" images...");
		}

		for (File file : images) {
			count ++;

			try {
				// Getting class names
				String fileName = file.getName();
				fileName.substring(0, fileName.indexOf("."));
				String classNames[] = fileName.split(classSeparator);
				System.out.println("Processing file: "+file.getName());

				// Processing features
				try {
					long time = System.currentTimeMillis();
					FeaturesExtract extracted = new FeaturesExtract(file.getAbsolutePath());
					extracted.extractFeatures(calibration);
					long extractFeaturesTime = System.currentTimeMillis() - time;

					// Extracting features for each classifier
					for (int i = 0; i < classifiers.length; i++) {
						if(i < classNames.length - 1 && classNames[i].length() < 4) {
							System.out.println("Extracting features for instance type: "+classNames[i]);
							if(progress != null)
								progress.updateMessage("Extracting features for instance type: "+classNames[i]);
							try {
								classifiers[i].insertInstanceFromFeatures(extracted, classNames[i]);
							} catch(Exception e) {
								try {
									System.err.println("Error inserting the instance "+classNames[classNames.length]+" for the classifier: "+classifiers[i].getClass().getName());
									if(progress != null)
										progress.updateMessage("Error inserting the instance "+classNames[classNames.length]+" for the classifier: "+classifiers[i].getClass().getName());
									e.printStackTrace();
								} catch(Exception e1) {}
							}
						} else {
							System.out.println("Not qualified for this classifier.");
						}
					}
					long classifiersTime = System.currentTimeMillis() - time - extractFeaturesTime;
					System.out.println("Processing time: "+extractFeaturesTime+" / "+classifiersTime);
					if(progress != null)
						progress.updateMessage("Processing time: "+extractFeaturesTime+" / "+classifiersTime);
				} catch(Exception e) {
					System.err.println("Error processing image: "+fileName);
					if(progress != null)
						progress.updateMessage("Error processing image: "+fileName);
					e.printStackTrace();
				}
			} catch(Exception e) {
				System.err.println("Error with file: "+file);
				e.printStackTrace();
			}
			System.out.println("Time: "+calculateTime(startTime));
			if(progress != null) {
				double percent = (count/((double)images.size()));
				progress.updateProgress(percent);
			} else {
				int percent = (int) ((count/((double)images.size()))*100);
				System.out.println("Processing "+count+" of "+images.size()+" ("+percent+"%)");
			}
		}

		// Saving data
		try {
			for (int i = 0; i < classifiers.length; i++) {
				File toSave = new File(destinationFile.getParentFile().getAbsolutePath()+"/ts"+i+"_"+destinationFile.getName());
				classifiers[i].saveTrainningSetIntoFile(toSave);
			}
		} catch (FileNotFoundException e) {
		}
		this.hasLoadedTrainingSet = true;
	}

	private static final long HOUR = 3600000l;
	private static final long MINUTE = 60000l;
	private static final long SECOND = 1000l;
	private String calculateTime(long startTime) {
		startTime = System.currentTimeMillis()-startTime;
		long hours = startTime/HOUR;
		startTime -= hours*HOUR; 
		long minutes = startTime/MINUTE;
		startTime -= minutes*MINUTE;
		long seconds = startTime/SECOND;
		return hours+":"+(minutes < 10 ? "0"+minutes : ""+minutes)+":"+(seconds < 10 ? "0"+seconds : ""+seconds);
	}

	/**
	 * Loads a training set from the given file.
	 * @param file Location of the file that contains the training set.
	 */
	public boolean loadTrainingSet(File file, String classifier, CameraCalibration calibration) {
		try {
			switch (classifier) {
			case QUALITY_CLASSIFIER:
				this.classifiers[0] = new QualityClassifier(calibration);
				this.classifiers[0].loadDataSetFromFile(file);
				break;

			case SIZE_CLASSIFIER:
				this.classifiers[1] = new SizeClassifier(calibration);
				this.classifiers[1].loadDataSetFromFile(file);
				break;

			case CLASS_CLASSIFIER:
				this.classifiers[2] = new ClassClassifier(calibration);
				this.classifiers[2].loadDataSetFromFile(file);
				break;

			case RIPENESS_CLASSIFIER:
				this.classifiers[3] = new RipenessClassifier(calibration);
				this.classifiers[3].loadDataSetFromFile(file);
				break;
			}
			this.hasLoadedTrainingSet = true;
			return true;
		} catch (IOException e) {
			this.hasLoadedTrainingSet = false;
			return false;
		}
	}

	/**
	 * This method can only be used if either processTrainingSet or loadTrainingSet have been called.
	 * This method is deprecated, instead you should generate the dataset and use the graphical interface of Weka knowing to be more powerful.
	 * @deprecated
	 * @param classifierDestination The destination file where the model will be saved.
	 * @param type The type of machine learning that will be created.
	 * @return If the operation was successfully finished.
	 */
	public boolean trainClassifier(File savedTrainingSet, File classifierDestination, String technique, String classifier) {
		if(hasLoadedTrainingSet) {
			Classifier classifierType = null;
			switch (technique) {
			case NAIVE_BAYES:
				classifierType = new NaiveBayes();
				break;
			}
			if(classifierType != null) {
				try {
					trainOneClassifier(savedTrainingSet, classifierType, classifierDestination, classifier);
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * @deprecated
	 * @param savedTrainingSet
	 * @param classifierType
	 * @param destinationFile
	 * @param classifier
	 * @throws Exception
	 */
	private void trainOneClassifier(File savedTrainingSet, Classifier classifierType, File destinationFile, String classifier) throws Exception {
		switch (classifier) {
		case QUALITY_CLASSIFIER:
			classifiers[0].trainClassifier(classifierType, savedTrainingSet, destinationFile);
			break;

		case SIZE_CLASSIFIER:
			classifiers[1].trainClassifier(classifierType, savedTrainingSet, destinationFile);
			break;

		case CLASS_CLASSIFIER:
			classifiers[2].trainClassifier(classifierType, savedTrainingSet, destinationFile);
			break;

		case RIPENESS_CLASSIFIER:
			classifiers[3].trainClassifier(classifierType, savedTrainingSet, destinationFile);
			break;
		}
	}

	/**
	 * This method was used to train classifiers, instead you should generate the dataset and use the graphical interface of Weka knowing to be more powerful.
	 * @deprecated
	 * @param classifierType
	 * @param destinationFile
	 * @throws Exception
	 */
	private void trainAll(Classifier classifierType, File destinationFile) throws Exception {
		for (int i = 0; i < classifiers.length; i++) {
			File toSave = new File(destinationFile.getParentFile().getAbsolutePath()+"/model"+i+"_"+destinationFile.getName());
			classifiers[i].trainClassifier(classifierType, null, toSave);
		}
	}

	// ************************ ACCESS METHODS ************************

	public boolean hasLoadedImages() {
		return hasLoadedImages;
	}

	public ArrayList<File> getImages() {
		return images;
	}

	// ************************ ACCESS METHODS ************************

}
