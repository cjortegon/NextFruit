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
public class WekaClassifier {

	private static final String LOAD_SAVE_PATH = "resources" + System.getProperty("line.separator") +
			"MachineLearning" + System.getProperty("line.separator"); 
	private static final String LOG_PATH = "resources" + System.getProperty("line.separator") + 
			"MachineLearning" + System.getProperty("line.separator") + "log.txt";

	private static Instances trainingSet;
	private static Instances testSet;
	private static ArrayList<Attribute> features;

	/**
	 * Class constructor.
	 */
	public WekaClassifier(){
		if(features == null){
			defineFeaturesVector();
		}
	}

	/**
	 * Initialize the features vector.
	 */
	public void defineFeaturesVector(){
		
		//****************************************************************
		//	Atributos temporales -> Falta complementar estos, con los 
		//	atributos faltantes que falta definir.
		//****************************************************************
		
		//	Create and Initialize Attributes
		
		ArrayList<String> qualityClassValues = new ArrayList<String>(4);
		qualityClassValues.add("5r");
		qualityClassValues.add("5v");
		qualityClassValues.add("er");
		qualityClassValues.add("fea");
		
		Attribute area = new Attribute("area");
		Attribute mean = new Attribute("mean");
		Attribute sD = new Attribute("standard-deviation");
		Attribute skewness = new Attribute("skewness");
		Attribute kurtosis = new Attribute("kurtosis");
		Attribute red = new Attribute("red-percentage");
		Attribute green = new Attribute("green-percentage");
		Attribute brown = new Attribute("brown-percentage");
		Attribute qualityClass = new Attribute("quality", qualityClassValues);

		
		//	Declare the feature vector
		features = new ArrayList<Attribute>(9);
		features.add(area);
		features.add(mean);
		features.add(sD);
		features.add(skewness);
		features.add(kurtosis);
		features.add(red);
		features.add(green);
		features.add(brown);
		features.add(qualityClass);
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
	public void saveDataSetIntoFile(Instances instances, String fileName){

		// Keeping a reference to the instances
		this.trainingSet = instances;

		// Saving the file
		try {
			PrintWriter writer = new PrintWriter(LOAD_SAVE_PATH + fileName.trim() + ".arff");
			writer.println(instances.toString());
			writer.close();
			writeLog("[saveDataSetIntoFile]: Se ha guardado la informacion del data set en " +
					"el archivo " + fileName + ".arff");
		} catch (FileNotFoundException e) {
			writeLog("[saveDataSetIntoFile]: No se pudo guardar el data set");
			e.printStackTrace();
		}
	}

	/**
	 * Loads a data set, contained in the given file received as a parameter.
	 * @param fileName, the file containing the training set data.
	 * @throws IOException
	 */
	public Instances loadDataSetFromFile(String fileName) throws IOException{		

		try {
			File dataToLoad = new File(LOAD_SAVE_PATH + fileName.trim() + ".arff");
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
	 */
	public void trainClassifier(Classifier classificationModel, String trainingSetFileName, String classifierSaveFileName){
		try {
			if(trainingSet == null){
				trainingSet = loadDataSetFromFile(trainingSetFileName);
			}

			classificationModel.buildClassifier(trainingSet);

			SerializationHelper.write(LOAD_SAVE_PATH + classifierSaveFileName.trim() + ".save", classificationModel);
			writeLog("[trainClassifier]: Se ha guardado correctamente el clasificador de tipo " +
					classificationModel.getClass().getSimpleName() + " en el archivo " + classifierSaveFileName.trim()+".save");

		} catch (Exception e) {
			writeLog("[trainClassifier]: El clasificador entrenado no pudo ser guardado");
			e.printStackTrace();
		}
	}

	/**
	 * Loads a classifier from a file in disk.
	 * @param fileName, the name of the file containing the classifier.
	 * @return classifier object loaded from file.
	 */
	public Classifier loadClassifierFromFile(String fileName){
		try {
			Classifier model = (Classifier) SerializationHelper.read(LOAD_SAVE_PATH + fileName.trim() + ".save");
			writeLog("[loadClassifierFromFile]: Se cargo correctamente el clasificador de tipo " + 
					model.getClass().getSimpleName() + " a partir del archivo " + fileName + ".save");
			return model;

		} catch (Exception e) {
			e.printStackTrace();
		}

		writeLog("[loadClassifierFromFile]: No pudo cargarse un clasificador a partir del " +
				"archivo especificado");
		return null;
	}

	/**
	 * Evaluates a classifier trained with a given training data, using a test data.
	 * @param classificationModel, the classifier to test.
	 * @param trainingSetFileName, the name of the file containing the training set data.
	 * @param testSetFileName, the name of the file containing the test set data.
	 */
	public void testClassifierModel(Classifier classificationModel, String trainingSetFileName, String testSetFileName){
		try {
			if(trainingSet == null){
				trainingSet = loadDataSetFromFile(trainingSetFileName);
			}
			if(testSet == null){
				testSet = loadDataSetFromFile(testSetFileName);
			}

			if(trainingSet != null && testSet != null){

				Evaluation ev = new Evaluation(trainingSet);
				ev.evaluateModel(classificationModel, testSet);
				saveEvaluationData(ev, classificationModel.getClass().getSimpleName() + "TestResults.txt");
				writeLog("[testClassifierModel]: Se finalizo correctamente la evaluacion del modelo " +
						classificationModel.getClass().getSimpleName());

			}else{
				writeLog("[testClassifierModel]: No se pudo evaluar el modelo");
			}

		} catch (Exception e) {

			writeLog("[testClassifierModel]: No se pudo evaluar el modelo");
			e.printStackTrace();
		}	
	}

	/**
	 * Given an evaluation of a classifier, saves the results to a file in disk.
	 * @param ev, evaluation object with the results data.
	 * @param fileName, name of the file in which the data is going to be saved.
	 */
	public void saveEvaluationData(Evaluation ev, String fileName){
		try {
			String separator = System.getProperty("line.separator");
			double[][] cmMatrix = ev.confusionMatrix();

			String s = ("**************" + separator + separator +
					" Test Results" + separator + separator +
					"**************" + separator + separator +
					separator + "-- SUMMARY --" + separator +
					ev.toSummaryString() + separator +
					separator + "-- CONFUSION MATRIX --" + separator + 

					//********************************************************
					//	Modificar la matriz de confusion para que se ajuste a 
					//	nuestro problema.
					//********************************************************

					separator + "Class-1" + "   " + "Class-2" + "Class-E"+ "   -> Se clasifico como" + separator +
					"(" + cmMatrix[0][0] + ")" + " (" + cmMatrix[0][1] + ")" + "   | Class-1 (Real Value)" + separator +
					"(" + cmMatrix[1][0] + ")" + " (" + cmMatrix[1][1] + ")" + "   | Class-2 (Rela Value)" + separator +
					"(" + cmMatrix[2][0] + ")" + " (" + cmMatrix[2][1] + ")" + "   | Class-E (Real Value)");

			PrintWriter writer = new PrintWriter(LOAD_SAVE_PATH + fileName.trim() + ".txt");
			writer.println(s);
			writer.close();
			writeLog("[saveEvaluationData]: Los resultados de la evaluacion se han guardado en " +
					"el archivo " + fileName + ".txt");

		} catch (FileNotFoundException e) {
			writeLog("[saveEvaluationData]: No se pudo guardar el resultado de la evaluacion ");
			e.printStackTrace();
		}
	}

	/**
	 * Auxiliary method to write a message into a log file. 
	 * @param message, the string to write into the file.
	 */
	private void writeLog(String message){
		try {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Files.write(Paths.get(LOG_PATH), ("[" + (dateFormat.format(Calendar.getInstance().getTime())) + "]" + message +
					System.getProperty("line.separator")).getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Attribute> getFeatures() {
		return features;
	}

}
