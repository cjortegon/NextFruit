package co.edu.icesi.frutificator.util;

import java.util.ArrayList;

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

	public double getStandardDeviation() {
		double differences = 0;
		double mean = getMean();
		for (int i = 0; i < set.size(); i++)
			differences += Math.pow(set.get(i) - mean, 2);
		differences /= set.size();
		return Math.sqrt(differences);
	}

}
