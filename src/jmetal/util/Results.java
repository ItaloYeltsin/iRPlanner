package jmetal.util;

import java.util.ArrayList;

import jmetal.core.Solution;
import jmetal.core.Variable;

public class Results {
	
	public double getAverage(ArrayList<Double> values) {
		double sum = 0;
		for (Double double1 : values) {
			sum += double1;
		}
		return sum/values.size();
	}
	
	/**
	 * Given a set of results this function return the standard Deviation.
	 * @param average
	 * @param values
	 * @return 
	 */	
	public double getStandardDeviation(double average, ArrayList<Double> values){
	      ArrayList<Double> deviances = getDeviances(average, values);
	      double variance = getVariance(deviances);

	      return Math.sqrt(variance);
	}
	
	/**
	 * 
	 * @param mean
	 * @param values
	 * @return deviances of the fiven numerical vector
	 */
	private ArrayList<Double> getDeviances(double mean, ArrayList<Double> values){
		ArrayList<Double> deviances = new ArrayList<Double>(values.size());

		for(int i = 0; i <= values.size() - 1; i++){
			deviances.add(mean - values.get(i));
		}

		return deviances;
	}
	/**
	 * 
	 * @param deviances
	 * @return Variances
	 */
	private double getVariance(ArrayList<Double> deviances){
		double quadraticDeviancesSum = 0;

		for(int i = 0; i <= deviances.size() - 1; i++){
			quadraticDeviancesSum += Math.pow(deviances.get(i), 2);
		}

		return quadraticDeviancesSum / deviances.size();
	}
}