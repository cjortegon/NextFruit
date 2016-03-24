package formulas;

import co.edu.icesi.nextfruit.util.Statistics;

public class StatisticsTest {

	public static void main(String[] args) {
		
		Statistics statistics = new Statistics();
		dataSet2(statistics);
		
		System.err.println("Average       : "+statistics.getMean());
		System.err.println("Std. deviation: "+statistics.getStandardDeviation());
		System.err.println("Skewness      : "+statistics.getSkewness());
		System.err.println("Kurtosis      : "+statistics.getKurtosis());
		
	}
	
	public static void dataSet1(Statistics statistics) {
		statistics.addValue(2);
		statistics.addValue(3);
		statistics.addValue(4);
		statistics.addValue(5);
		statistics.addValue(5);
		statistics.addValue(5);
		statistics.addValue(5);
		statistics.addValue(6);
		statistics.addValue(7);
		statistics.addValue(8);
	}
	
	public static void dataSet2(Statistics statistics) {
		statistics.addValue(2);
		statistics.addValue(3);
		statistics.addValue(3);
		statistics.addValue(4);
		statistics.addValue(4);
		statistics.addValue(4);
		statistics.addValue(5);
		statistics.addValue(5);
		statistics.addValue(6);
		statistics.addValue(7);
	}

}
