package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;

public class GA_main {

	public static void main(String [] args) throws JMException, ClassNotFoundException {
		if(args == null || args.length == 0){
			throw new IllegalArgumentException("You should pass the instance file. Example ' -i example.rp'");
		
		}
	  
	  
	  
	    Problem   problem = new ReleasePlanningProblem("in/example_cos2_req10_rel3.rp");
	 
	    Algorithm algorithm = new InterativeGA(problem);
	  
	    /* Algorithm parameters*/
	    algorithm.setInputParameter("populationSize",100);
	    algorithm.setInputParameter("maxGenerations", 200);
	    algorithm.setInputParameter("elitismRate", 0.2);
	    algorithm.setInputParameter("feedBackPeriod", 4);
		algorithm.setInputParameter("numberOfFeedBacks", 1);
		algorithm.setInputParameter("feedBackGeneration", 100);
		algorithm.setInputParameter("alpha", 1.0);
		
		/* Add the operators to the algorithm*/
		algorithm.addOperator("crossover", getCrossoverOperator());
		algorithm.addOperator("mutation", getMutationOperator());
		algorithm.addOperator("selection", getSelectionOperator());
			
	    /* Execute the Algorithm */
	    long startTime = System.currentTimeMillis();
	    SolutionSet population = algorithm.execute();
	    long duration = System.currentTimeMillis() - startTime;
	    
	    /* Log messages */
	    System.out.println("Objectives values have been writen to file FUN");
	    population.printObjectivesToFile("FUN");
	    System.out.println("Variables values have been writen to file VAR");
	    population.printVariablesToFile("VAR");
	    System.out.println("Time was: "+duration+" ms");	    
	}
  
  	@SuppressWarnings({ "rawtypes", "unchecked" })
  	public static Operator getMutationOperator() throws JMException{
  		HashMap parameters = new HashMap() ;
 	    parameters.put("probability", 0.01) ;
 	    
 	    return MutationFactory.getMutationOperator("BitFlipMutation", parameters);
  	}
  	
  	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Operator getCrossoverOperator() throws JMException{
  		HashMap parameters = new HashMap() ;
	    parameters.put("probability", 0.9) ;
	    
	    return CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);
  	}

  	public static Operator getSelectionOperator() throws JMException{
	    return SelectionFactory.getSelectionOperator("BinaryTournament", null) ;
	}
}
