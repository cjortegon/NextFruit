package demos;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


/**
 * This is an example class using WEKA to solve a classification problem.
 * In this class a features vector is created, a training set is defined 
 * and a classifier created using said training set. Said classifier is 
 * tested by using a test set. There's also a method to classify a given
 * unknown case using the trained classifier. The information is displayed
 * via console.
 * @author JuanD
 *
 */
public class WeatherExample {

	static Classifier classificationModel; 
	
	
	/**
	 * Application's entry point method
	 * @param args
	 */
	public static void main(String args[]){
		
		try {
			
			//*******************************
			//	Create a classification model
			//*******************************
			
			ArrayList<Attribute> features = defineFeaturesVector();
			Instances trainingSet = defineTrainingSet(features);
			classificationModel = trainClassifier(trainingSet);
			
			
			//*******************************
			//	Test the classification model
			//*******************************
			
			Instances testSet = defineTestSet(features);
			Evaluation test = testClassifierModel(classificationModel, trainingSet, testSet);
			
			
			//*******************************
			//	Classify a unknown instance
			//*******************************
			
			//	Particular Example Case
			
			Instance unknown = new DenseInstance(4);
			unknown.setValue(features.get(0), "Sunny");
			unknown.setValue(features.get(1), 60);
			unknown.setValue(features.get(2), 50);
			unknown.setValue(features.get(3), "TRUE");
			unknown.setDataset(trainingSet);
			
			double[] fDistribution = classify(unknown);
			
			
			//*******************************
			//	Print Information Via Console
			//*******************************
			
			printConsoleInfo(trainingSet, test, fDistribution);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Defines features vector structure
	 * @return
	 */
	private static ArrayList<Attribute> defineFeaturesVector(){
		
		//	Create and Initialize Attributes
	
		ArrayList<String> outlookValues = new ArrayList<String>(3);
		outlookValues.add("Sunny");
		outlookValues.add("Overcast");
		outlookValues.add("Rainy");
		
		ArrayList<String> windyValues = new ArrayList<String>(2);
		windyValues.add("TRUE");
		windyValues.add("FALSE");
		
		ArrayList<String> playClassValues = new ArrayList<String>(2);
		playClassValues.add("yes");
		playClassValues.add("no");
		
		Attribute outlook = new Attribute("outlook", outlookValues);
		Attribute temperature = new Attribute("temperature");
		Attribute humidity = new Attribute("humidity");
		Attribute windy = new Attribute("windy", windyValues);
		Attribute playClass = new Attribute("play", playClassValues);

		//	Declare the feature vector
		
		ArrayList<Attribute> features = new ArrayList<Attribute>(5);
		features.add(outlook);
		features.add(temperature);
		features.add(humidity);
		features.add(windy);
		features.add(playClass);
		
		return features;
		
	}
	
	
	/**
	 * Creates a training set
	 * In this particular example, the test case being used is the same training set
	 * @param features
	 * @return
	 */
	private static Instances defineTrainingSet(ArrayList<Attribute> features){
		
		Instances trainingSet = new Instances("weather", features, 14);
		trainingSet.setClassIndex(4);
		
		//	Create concrete instances
		
		Instance i1 = new DenseInstance(5);
		i1.setValue(features.get(0), "Sunny");
		i1.setValue(features.get(1), 85);
		i1.setValue(features.get(2), 85);
		i1.setValue(features.get(3), "FALSE");
		i1.setValue(features.get(4), "no");

		Instance i2 = new DenseInstance(5);
		i2.setValue(features.get(0), "Sunny");
		i2.setValue(features.get(1), 80);
		i2.setValue(features.get(2), 90);
		i2.setValue(features.get(3), "TRUE");
		i2.setValue(features.get(4), "no");
		
		Instance i3 = new DenseInstance(5);
		i3.setValue(features.get(0), "Overcast");
		i3.setValue(features.get(1), 83);
		i3.setValue(features.get(2), 86);
		i3.setValue(features.get(3), "FALSE");
		i3.setValue(features.get(4), "yes");
		
		Instance i4 = new DenseInstance(5);
		i4.setValue(features.get(0), "Rainy");
		i4.setValue(features.get(1), 70);
		i4.setValue(features.get(2), 96);
		i4.setValue(features.get(3), "FALSE");
		i4.setValue(features.get(4), "yes");

		Instance i5 = new DenseInstance(5);
		i5.setValue(features.get(0), "Rainy");
		i5.setValue(features.get(1), 68);
		i5.setValue(features.get(2), 80);
		i5.setValue(features.get(3), "FALSE");
		i5.setValue(features.get(4), "yes");
		
		Instance i6 = new DenseInstance(5);
		i6.setValue(features.get(0), "Rainy");
		i6.setValue(features.get(1), 65);
		i6.setValue(features.get(2), 70);
		i6.setValue(features.get(3), "TRUE");
		i6.setValue(features.get(4), "no");
		
		Instance i7 = new DenseInstance(5);
		i7.setValue(features.get(0), "Overcast");
		i7.setValue(features.get(1), 64);
		i7.setValue(features.get(2), 65);
		i7.setValue(features.get(3), "TRUE");
		i7.setValue(features.get(4), "yes");
		
		Instance i8 = new DenseInstance(5);
		i8.setValue(features.get(0), "Sunny");
		i8.setValue(features.get(1), 72);
		i8.setValue(features.get(2), 95);
		i8.setValue(features.get(3), "FALSE");
		i8.setValue(features.get(4), "no");

		Instance i9 = new DenseInstance(5);
		i9.setValue(features.get(0), "Sunny");
		i9.setValue(features.get(1), 69);
		i9.setValue(features.get(2), 70);
		i9.setValue(features.get(3), "FALSE");
		i9.setValue(features.get(4), "yes");
		
		Instance i10 = new DenseInstance(5);
		i10.setValue(features.get(0), "Rainy");
		i10.setValue(features.get(1), 75);
		i10.setValue(features.get(2), 80);
		i10.setValue(features.get(3), "FALSE");
		i10.setValue(features.get(4), "yes");
		
		Instance i11 = new DenseInstance(5);
		i11.setValue(features.get(0), "Sunny");
		i11.setValue(features.get(1), 75);
		i11.setValue(features.get(2), 70);
		i11.setValue(features.get(3), "TRUE");
		i11.setValue(features.get(4), "yes");

		Instance i12 = new DenseInstance(5);
		i12.setValue(features.get(0), "Overcast");
		i12.setValue(features.get(1), 72);
		i12.setValue(features.get(2), 90);
		i12.setValue(features.get(3), "TRUE");
		i12.setValue(features.get(4), "yes");
		
		Instance i13 = new DenseInstance(5);
		i13.setValue(features.get(0), "Overcast");
		i13.setValue(features.get(1), 81);
		i13.setValue(features.get(2), 75);
		i13.setValue(features.get(3), "FALSE");
		i13.setValue(features.get(4), "yes");
		
		Instance i14 = new DenseInstance(5);
		i14.setValue(features.get(0), "Rainy");
		i14.setValue(features.get(1), 71);
		i14.setValue(features.get(2), 91);
		i14.setValue(features.get(3), "TRUE");
		i14.setValue(features.get(4), "no");
		
		//	Add concrete instances to the training set
		
		trainingSet.add(i1);
		trainingSet.add(i2);
		trainingSet.add(i3);
		trainingSet.add(i4);
		trainingSet.add(i5);
		trainingSet.add(i6);
		trainingSet.add(i7);
		trainingSet.add(i8);
		trainingSet.add(i9);
		trainingSet.add(i10);
		trainingSet.add(i11);
		trainingSet.add(i12);
		trainingSet.add(i13);
		trainingSet.add(i14);
		
		return trainingSet;

	}
	
	
	/**
	 * creates a test set
	 * 
	 * @param features
	 * @return
	 */
	private static Instances defineTestSet(ArrayList<Attribute> features){
		
		Instances testSet = new Instances("weather", features, 14);
		testSet.setClassIndex(4);
		
		//	Create concrete instances
		
		Instance i1 = new DenseInstance(5);
		i1.setValue(features.get(0), "Sunny");
		i1.setValue(features.get(1), 85);
		i1.setValue(features.get(2), 85);
		i1.setValue(features.get(3), "FALSE");
		i1.setValue(features.get(4), "no");

		Instance i2 = new DenseInstance(5);
		i2.setValue(features.get(0), "Sunny");
		i2.setValue(features.get(1), 80);
		i2.setValue(features.get(2), 90);
		i2.setValue(features.get(3), "TRUE");
		i2.setValue(features.get(4), "no");
		
		Instance i3 = new DenseInstance(5);
		i3.setValue(features.get(0), "Overcast");
		i3.setValue(features.get(1), 83);
		i3.setValue(features.get(2), 86);
		i3.setValue(features.get(3), "FALSE");
		i3.setValue(features.get(4), "yes");
		
		Instance i4 = new DenseInstance(5);
		i4.setValue(features.get(0), "Rainy");
		i4.setValue(features.get(1), 70);
		i4.setValue(features.get(2), 96);
		i4.setValue(features.get(3), "FALSE");
		i4.setValue(features.get(4), "yes");

		Instance i5 = new DenseInstance(5);
		i5.setValue(features.get(0), "Rainy");
		i5.setValue(features.get(1), 68);
		i5.setValue(features.get(2), 80);
		i5.setValue(features.get(3), "FALSE");
		i5.setValue(features.get(4), "yes");
		
		Instance i6 = new DenseInstance(5);
		i6.setValue(features.get(0), "Rainy");
		i6.setValue(features.get(1), 65);
		i6.setValue(features.get(2), 70);
		i6.setValue(features.get(3), "TRUE");
		i6.setValue(features.get(4), "no");
		
		Instance i7 = new DenseInstance(5);
		i7.setValue(features.get(0), "Overcast");
		i7.setValue(features.get(1), 64);
		i7.setValue(features.get(2), 65);
		i7.setValue(features.get(3), "TRUE");
		i7.setValue(features.get(4), "yes");
		
		Instance i8 = new DenseInstance(5);
		i8.setValue(features.get(0), "Sunny");
		i8.setValue(features.get(1), 72);
		i8.setValue(features.get(2), 95);
		i8.setValue(features.get(3), "FALSE");
		i8.setValue(features.get(4), "no");

		Instance i9 = new DenseInstance(5);
		i9.setValue(features.get(0), "Sunny");
		i9.setValue(features.get(1), 69);
		i9.setValue(features.get(2), 70);
		i9.setValue(features.get(3), "FALSE");
		i9.setValue(features.get(4), "yes");
		
		Instance i10 = new DenseInstance(5);
		i10.setValue(features.get(0), "Rainy");
		i10.setValue(features.get(1), 75);
		i10.setValue(features.get(2), 80);
		i10.setValue(features.get(3), "FALSE");
		i10.setValue(features.get(4), "yes");
		
		Instance i11 = new DenseInstance(5);
		i11.setValue(features.get(0), "Sunny");
		i11.setValue(features.get(1), 75);
		i11.setValue(features.get(2), 70);
		i11.setValue(features.get(3), "TRUE");
		i11.setValue(features.get(4), "yes");

		Instance i12 = new DenseInstance(5);
		i12.setValue(features.get(0), "Overcast");
		i12.setValue(features.get(1), 72);
		i12.setValue(features.get(2), 90);
		i12.setValue(features.get(3), "TRUE");
		i12.setValue(features.get(4), "yes");
		
		Instance i13 = new DenseInstance(5);
		i13.setValue(features.get(0), "Overcast");
		i13.setValue(features.get(1), 81);
		i13.setValue(features.get(2), 75);
		i13.setValue(features.get(3), "FALSE");
		i13.setValue(features.get(4), "yes");
		
		Instance i14 = new DenseInstance(5);
		i14.setValue(features.get(0), "Rainy");
		i14.setValue(features.get(1), 71);
		i14.setValue(features.get(2), 91);
		i14.setValue(features.get(3), "TRUE");
		i14.setValue(features.get(4), "no");
		
		//	Add concrete instances to the training set
		
		testSet.add(i1);
		testSet.add(i2);
		testSet.add(i3);
		testSet.add(i4);
		testSet.add(i5);
		testSet.add(i6);
		testSet.add(i7);
		testSet.add(i8);
		testSet.add(i9);
		testSet.add(i10);
		testSet.add(i11);
		testSet.add(i12);
		testSet.add(i13);
		testSet.add(i14);
		
		return testSet;

	}
	
	
	/**
	 * Trains a classifier given a training set
	 * @param trainingSet
	 * @return
	 * @throws Exception
	 */
	private static Classifier trainClassifier(Instances trainingSet) throws Exception{
		
		//	Using a Naive Bayes classifier
		
		Classifier classificationModel = new NaiveBayes();
		classificationModel.buildClassifier(trainingSet);
		
		return classificationModel;
	} 
	
	
	/**
	 * Test the classification model, using a test set with known values
	 * @param model
	 * @param trainingSet
	 * @param testSet
	 * @return
	 * @throws Exception
	 */
	private static Evaluation testClassifierModel(Classifier model, Instances trainingSet, Instances testSet) throws Exception{
		
		Evaluation ev = new Evaluation(trainingSet);
		ev.evaluateModel(model, testSet);
		return ev;
	}
	
	
	/**
	 * Classify a given unknow case
	 * @param unknown
	 * @return
	 * @throws Exception
	 */
	private static double [] classify(Instance unknown) throws Exception{
	
		// Get the likelihood of each classes
		// fDistribution[0] is the probability of being “positive”
		// fDistribution[1] is the probability of being “negative”
		
		double[] fDistribution = classificationModel.distributionForInstance(unknown);
		return fDistribution;
	}
	
	
	/**
	 * Print a summary of the information via console
	 * @param trainingSet
	 * @param ev
	 * @param fDistribution
	 */
	private static void printConsoleInfo(Instances trainingSet, Evaluation ev, double[] fDistribution){
		
		System.out.println("*******************");
		System.out.println(" ARFF File Content");
		System.out.println("*******************");
		
		System.out.println();
		System.out.println(trainingSet.toString());
		System.out.println();
		
		System.out.println("*******************");
		System.out.println(" Test Results");
		System.out.println("*******************");
		
		System.out.println();
		System.out.println("-- SUMMARY --");
		System.out.println();
		System.out.println(ev.toSummaryString());
		System.out.println();
		System.out.println("-- CONFUSION MATRIX	--");
		System.out.println();
		double[][] cmMatrix = ev.confusionMatrix();
		System.out.println("YES" + "   " + "NO" + "   -> Se clasificó como");
		System.out.println("(" + cmMatrix[0][0] + ")" + " (" + cmMatrix[0][1] + ")" +
		"   | YES (Valor Real)");
		System.out.println("(" + cmMatrix[1][0] + ")" + " (" + cmMatrix[1][1] + ")" +
		"   | NO (Valor Real)");
		System.out.println();
		
		System.out.println("*******************");
		System.out.println(" Classification Result");
		System.out.println("*******************");
		
		System.out.println();
		System.out.println("YES -> " + fDistribution[0]);
		System.out.println("NO  -> " + fDistribution[1]);
		System.out.println();
		
		System.out.println("*******************");
		System.out.println(" END");
		System.out.println("*******************");
		
	}
	
}
