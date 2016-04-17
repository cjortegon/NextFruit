package co.edu.icesi.nextfruit.modules.machinelearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import co.edu.icesi.nextfruit.modules.model.CameraCalibration;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * This class provides methods to handle the classification functionality using the
 * WEKA library.
 * @author JuanD
 *
 */
public abstract class WekaClassifier {

	private static final String LOG_PATH = "resources" + File.separator + "log.txt";

	private ArrayList<Attribute> features;
	protected Instances trainingSet;
	protected Instances testSet;
	protected CameraCalibration calibration;

	/**
	 * Class constructor.
	 */
	public WekaClassifier(CameraCalibration calibration) {
		
		this.calibration = calibration;
		File temp = new File(LOG_PATH);

		if(!temp.exists()){
			try {
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		features = defineFeaturesVector();
	}


	/**
	 * Classify a given unknown instance, which is the first element in an Instances object
	 * received as a parameter.
	 * @param Classifier, the classifier object to be used.
	 * @param dataUnlabeled, Instances object containing the unknown instance to classify.
	 * @return double array, 
	 * 			index[0] -> probability of being positive
	 * 			index[1] -> probability of being negative
	 * @throws Exception
	 */
	public double[] classify(Classifier classificationModel, Instances dataUnlabeled)
			throws Exception{

		dataUnlabeled.setClassIndex(dataUnlabeled.numAttributes() - 1);

		// Get the likelihood of each classes
		// fDistribution[0] is the probability of being positive
		// fDistribution[1] is the probability of being negative

		double[] fDistribution = classificationModel.distributionForInstance(dataUnlabeled.firstInstance());
		return fDistribution;
	}

	/**
	 * Saves an ARFF file that contains the data set received as a parameter.
	 * @param setToSave, Instances object containing the data set.
	 * @param fileName, name of the ARFF file
	 * @throws FileNotFoundException
	 */
	public void saveDataSetIntoFile(Instances instances, File file) throws FileNotFoundException{

		// Keeping a reference to the instances
		this.trainingSet = instances;

		// Saving the file
		try {
			PrintWriter writer = new PrintWriter(file.getAbsolutePath() + ".arff");
			writer.println(instances.toString());
			writer.close();
			writeLog("[saveDataSetIntoFile]: Se ha guardado la informacion del data set en " +
					"el archivo " + file.getName() + ".arff");
		} catch (FileNotFoundException e) {
			writeLog("[saveDataSetIntoFile]: No se pudo guardar el data set");
			e.printStackTrace();
			throw e;
		}
	}

	public void saveTrainningSetIntoFile(File file) throws FileNotFoundException {
		saveDataSetIntoFile(trainingSet, file);
	}

	public void loadDataSetFromFile(File dataToLoad) throws IOException {
		trainingSet = getDataSetFromFile(dataToLoad);
	}

	/**
	 * Loads a data set, contained in the given file received as a parameter.
	 * @param fileName, the file containing the training set data.
	 * @throws IOException
	 */
	public Instances getDataSetFromFile(File dataToLoad) throws IOException {
		String fileName = dataToLoad.getName();
		try {
			FileReader fReader = new FileReader(dataToLoad);
			BufferedReader bReader = new BufferedReader(fReader);
			Instances returnValue = new Instances(bReader);
			returnValue.setClassIndex(returnValue.numAttributes()-1);

			fReader.close();
			bReader.close();

			writeLog("[LoadDataSetFromFile]: Se cargo correctamente el archivo " + fileName.trim() + ".arff");
			return returnValue;

		} catch (IOException e) {
			e.printStackTrace();
			writeLog("[LoadDataSetFromFile]: No se pudo cargar el archivo " + fileName.trim() + ".arff");	
			throw e;
		}
	}

	/**
	 * Train a classifier object given a training data set, and save the classifier to a file 
	 * in disk.
	 * @param classificationModel, the model to be used to train the classifier.
	 * @param trainingSetFileName, the name of the file containing the training set.
	 * @param classifierSaveFileName, the name of the file to save the classifier into.
	 * @throws Exception 
	 */
	public void trainClassifier(Classifier classificationModel, File trainingSetFileName, File classifierSaveFile) throws Exception {
		try {
			if(trainingSet == null) {
				loadDataSetFromFile(trainingSetFileName);
				if(trainingSetFileName == null)
					throw new Exception("No trainning file loaded.");
			}

			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
			classificationModel.buildClassifier(trainingSet);

			SerializationHelper.write(classifierSaveFile.getAbsolutePath() + ".save", classificationModel);
			writeLog("[trainClassifier]: Se ha guardado correctamente el clasificador de tipo " +
					classificationModel.getClass().getSimpleName() + " en el archivo " + classifierSaveFile.getName()+".save");

		} catch (Exception e) {
			writeLog("[trainClassifier]: El clasificador entrenado no pudo ser guardado");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Loads a classifier from a file in disk.
	 * @param fileName, the name of the file containing the classifier.
	 * @return classifier object loaded from file.
	 * @throws Exception 
	 */
	public Classifier loadClassifierFromFile(File fileToLoad) throws Exception {
		try {
			Classifier model = (Classifier) SerializationHelper.read(fileToLoad.getAbsolutePath());
			writeLog("[loadClassifierFromFile]: Se cargo correctamente el clasificador de tipo " + 
					model.getClass().getSimpleName() + " a partir del archivo " + fileToLoad.getName() + ".");
			return model;

		} catch (Exception e) {
			writeLog("[loadClassifierFromFile]: No pudo cargarse un clasificador a partir del " +
					"archivo especificado");
			throw e;
		}
	}

	/**
	 * Evaluates a classifier trained with a given training data, using a test data.
	 * @param classificationModel, the classifier to test.
	 * @param trainingSetFile, file containing the training set data.
	 * @param testSetFile, file containing the test set data.
	 * @param testResults, file into which the test results will be saved.
	 */
	public Evaluation testClassifierModel(Classifier classificationModel, File trainingSetFile, 
			File testSetFile, File testResults){

		Evaluation ev = null;

		try {
			if(trainingSet == null){
				trainingSet = getDataSetFromFile(trainingSetFile);
			}
			if(testSet == null){
				testSet = getDataSetFromFile(testSetFile);
			}

			if(trainingSet != null && testSet != null){

				ev = new Evaluation(trainingSet);
				ev.evaluateModel(classificationModel, testSet);
				saveEvaluationData(ev, testResults);
				writeLog("[testClassifierModel]: Se finalizo correctamente la evaluacion del modelo " +
						classificationModel.getClass().getSimpleName());

			} else {
				writeLog("[testClassifierModel]: No se pudo evaluar el modelo");
			}

		} catch (Exception e) {

			writeLog("[testClassifierModel]: No se pudo evaluar el modelo");
			e.printStackTrace();
		}	

		return ev;
	}

	public ArrayList<Attribute> getFeatures() {
		return features;
	}


	/**
	 * Initialize the features vector.
	 */
	protected abstract ArrayList<Attribute> defineFeaturesVector();

	/**
	 * Given an evaluation of a classifier, saves the results to a file in disk.
	 * @param ev, evaluation object with the results data.
	 * @param file, file in which the data is going to be saved.
	 */
	protected abstract void saveEvaluationData(Evaluation ev, File file);


	/**
	 * Auxiliary method to write a message into a log file. 
	 * @param message, the string to write into the file.
	 */
	public void writeLog(String message){
		try {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			System.out.println(LOG_PATH);
			Files.write(Paths.get(LOG_PATH), ("[" + (dateFormat.format(Calendar.getInstance().getTime())) + "]" + message +
					System.getProperty("line.separator")).getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
