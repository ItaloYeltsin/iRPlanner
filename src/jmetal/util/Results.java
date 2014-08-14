package jmetal.util;

import jmetal.core.Solution;
import jmetal.core.Variable;

public class Results {
	int[][] constraints;
	
	public void setConstraints(int[][] constraints) {
		this.constraints = constraints;
	}
	
	public double getPreferenceRate(Solution solution) throws JMException {
		Variable[] individual = solution.getDecisionVariables();
		double rate = 0;
		for (int i = 0; i < constraints.length; i++) {
			int r1 = constraints[i][0];
			int r2 = constraints[i][1];
			int hasToBeTogether = constraints[i][2];
			
			if(hasToBeTogether == 1) {
				if(individual[r1].getValue() == individual[r2].getValue()) {
					rate++;
				}
			}
			else{
				if(individual[r1].getValue() != individual[r2].getValue()) 
					rate++;
			}
		}
		System.out.println(rate/(double)constraints.length);
		return rate/(double)constraints.length;
	}
}