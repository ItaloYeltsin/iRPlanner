package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import jmetal.config.IGA;
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
import org.apache.commons.io.FilenameUtils;

public class GA_main {
		
	public static String filename;
	
	public static int populationSize = IGA.POPULATION_SIZE;
	
	public static int maxGenerations = IGA.MAX_GENERATIONS;
	
	public static double elitismRate = IGA.ELITISM_RATE;

	public static void main(String[] args) throws JMException, ClassNotFoundException, FileNotFoundException {
		// Example: java GA_main -i example.rp
		if (!isCorrectCommandLine(args)) {
			return;
		}
		
		loadProperties();
		
		System.out.println("IGA to Software Release Planning");
		System.out.println("Instance: " + filename);
		System.out.println();
		
		Problem problem = new ReleasePlanningProblem(filename);
	 
	    Algorithm algorithm = new InterativeGA(problem);
	  
	    /* Algorithm parameters*/
	    algorithm.setInputParameter("populationSize",populationSize);
	    algorithm.setInputParameter("maxGenerations", maxGenerations);
	    algorithm.setInputParameter("elitismRate", elitismRate);
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
	
	public static void loadProperties(){
		Properties properties = new Properties();
		
		try {
			String basename = FilenameUtils.getBaseName(filename);
			properties.load(new FileInputStream("conf/" + basename + ".config"));
			
			populationSize = Integer.valueOf(properties.getProperty("populationSize",String.valueOf(IGA.POPULATION_SIZE)));
			maxGenerations = Integer.valueOf(properties.getProperty("maxGenerations", String.valueOf(IGA.MAX_GENERATIONS)));
			elitismRate = Double.valueOf(properties.getProperty("elitismRate", String.valueOf(IGA.ELITISM_RATE)));
			
		} catch (IOException e) {
			//The instance doesn't have a config file
			//We will use the default parameters
		} 	
	}
	
	public static boolean isCorrectCommandLine(String[] args){
		//Define the command line 
		Options options = new Options();
		options.addOption("i", true, "The instance filename");
		
		try {
			CommandLineParser parser = new BasicParser();
			CommandLine cmd = parser.parse( options, args);
			
			if (cmd.hasOption("i")) {
				filename = cmd.getOptionValue("i");
				return true;
			}			
		} catch (ParseException e) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp("java GA_main -i example.rp", options);			
		}	
		
		return false;		
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
