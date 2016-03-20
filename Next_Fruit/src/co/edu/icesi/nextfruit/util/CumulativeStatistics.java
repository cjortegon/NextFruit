package co.edu.icesi.nextfruit.util;

import java.util.ArrayList;
import java.util.Collections;

public class CumulativeStatistics {

	private ArrayList<double[]> set;
	private double sum;
	private int count;

	public CumulativeStatistics() {
		this.set = new ArrayList<>();
	}

	public void addValue(double value, int count) {
		if(count <= 0)
			throw new IndexOutOfBoundsException();
		this.set.add(new double[]{value, count});
		this.sum += value*count;
		this.count += count;
	}

	public double getMean() {
		return sum/count;
	}

	public double getMedian() {
		//		if(set.size() == 0)
		//			throw new IndexOutOfBoundsException();
		//		Collections.sort(set);
		//		return set.get((set.size()-1)/2);
		throw new RuntimeException("Not implemented");
	}

	public double getStandardDeviation() {
		double differences = 0;
		double mean = getMean();
		for (int i = 0; i < set.size(); i++)
			differences += Math.pow(set.get(i)[0] - mean, 2)*set.get(i)[1];
		differences /= count;
		return Math.sqrt(differences);
	}

}