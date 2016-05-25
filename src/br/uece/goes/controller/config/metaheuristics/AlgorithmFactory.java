package br.uece.goes.controller.config.metaheuristics;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.metaheuristics.iga.IGA;

public class AlgorithmFactory {
	private AlgorithmFactory() {
		
	}
	
	public final static String GA = "Genetic Algorithm";
	
	public static Algorithm make(String algorithm, Problem problem) {
		if(GA.equals(algorithm)) {
			return new IGA(problem);
		}
		
		return null;
	}
	
	public static String instanceOf(Algorithm algorithm) {
		if(IGA.class.isInstance(algorithm)) {
			return GA;
		}
		
		return null;
	}
}
