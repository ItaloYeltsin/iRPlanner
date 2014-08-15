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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class GA_main {
		
	public static String filename;

	public static void main(String [] args) throws JMException, ClassNotFoundException {
		//Define the command line 
		Options options = new Options();
		options.addOption("i", true, "The instance filename");
		
		try {
			CommandLineParser parser = new BasicParser();
			CommandLine cmd = parser.parse( options, args);
			
			if (cmd.hasOption("i")) {
				filename = cmd.getOptionValue("i");				
			} else {
				throw new ParseException("Doesn't have option 'i'");
			}
		} catch (ParseException e) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp("java rp-iga -i example.rp", options);
			return;
		}		
		
		System.out.println("IGA to Software Release Planning");
		System.out.println("Filename: "+filename);
		System.out.println();
		
		Problem problem = new ReleasePlanningProblem(filename);
	 
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
