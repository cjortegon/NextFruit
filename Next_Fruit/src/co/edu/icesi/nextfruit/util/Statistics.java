package co.edu.icesi.nextfruit.util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class calculates the statistics from a give list of numbers.
 * @author cjortegon
 */
public class Statistics {

	private ArrayList<Double> set;
	private double sum;

	public Statistics() {
		this.set = new ArrayList<>();
	}

	public void addValue(double value) {
		this.set.add(value);
		this.sum += value;
	}

	public double getMean() {
		return sum/set.size();
	}

	public double getMedian() {
		if(set.size() == 0)
			throw new IndexOutOfBoundsException();
		Collections.sort(set);
		return set.get((set.size()-1)/2);
	}

	public double getStandardDeviation() {
		double sum = 0;
		double x = getMean();
		for (int i = 0; i < set.size(); i++)
			sum += Math.pow(set.get(i) - x, 2);
		sum /= (set.size()-1);
		return Math.sqrt(sum);
	}

	public double getSkewness() {
		double sum = 0;
		double x = getMean();
		double s = getStandardDeviation();
		for (int i = 0; i < set.size(); i++)
			sum += Math.pow((set.get(i) - x)/s, 3);
		double n = set.size();
		return (n/((n-1)*(n-2)))*sum;
	}

	public double getKurtosis() {
		double sum = 0;
		double x = getMean();
		double s = getStandardDeviation();
		for (int i = 0; i < set.size(); i++)
			sum += Math.pow((set.get(i) - x)/s, 4);
		double n = set.size();
		return ((n*(n+1))/((n-1)*(n-2)*(n-3)))*sum-((3*Math.pow(n-1, 2))/((n-2)*(n-3)));
	}

}
