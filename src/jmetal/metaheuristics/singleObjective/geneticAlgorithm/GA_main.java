package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.iga.IGA;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.print.results.PrintBestSolution;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;
import jmetal.util.Results;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.junit.runner.Result;

public class GA_main {
		
	public static String filename;
	
	public static double elitismRate = IGA.ELITISM_RATE;
	
	public static int populationSize = IGA.POPULATION_SIZE;
	
	public static int maxGenerations = IGA.MAX_GENERATIONS;
	
	public static int nGens = IGA.N_GENS;
	
	public static int nFeedback = IGA.N_FEEDBACK;
	
	public static int nIteractions = IGA.N_ITERACTIONS;

	public static void main(String[] args) throws JMException, ClassNotFoundException, IOException {
		// Example: java GA_main -i example.rp
		if (!isCorrectCommandLine(args)) {
			return;
		}
		
		loadProperties();
		
		System.out.println("IGA to Software Release Planning");
		System.out.println("Instance: " + filename);
		System.out.println();
		
		ReleasePlanningProblem problem = new ReleasePlanningProblem(filename);
		IGA algorithm = new IGA(problem);
	    
	    /* Algorithm parameters*/
	    algorithm.setInputParameter("populationSize",populationSize);
	    algorithm.setInputParameter("maxGenerations", maxGenerations);
	    algorithm.setInputParameter("elitismRate", elitismRate);
	    algorithm.setInputParameter("nGens", nGens);
		algorithm.setInputParameter("nFeedback", nFeedback);
		algorithm.setInputParameter("nIteractions", nIteractions);
		algorithm.setInputParameter("alpha", 1.0);
		
		/* Add the operators to the algorithm*/
		algorithm.addOperator("crossover", getCrossoverOperator());
		algorithm.addOperator("mutation", getMutationOperator());
		algorithm.addOperator("selection", getSelectionOperator());
			
	    /* Execute the Algorithm */
	    //SolutionSet population = algorithm.execute();
	    
	    
	    /* Log messages */
	  /*  System.out.println("Objectives values have been writen to file FUN");
	    population.printObjectivesToFile("FUN");
	    System.out.println("Variables values have been writen to file VAR");
	    population.printVariablesToFile("VAR");
	    System.out.println("Time: "+duration+" ms");
	    
	    System.out.println();
	    */
	    /*
	     * Avaluation
	     */
		
		problem.setAlpha(1.0);
		problem.setSimulator("in/data-set-1.pref");
		
	   	double average_score = 0;
	   	double average_utility = 0;
	   	double average_pref = 0;
	   	
	   	double standardDeviation_utility = 0;
	   	double standardDeviation_score = 0;
	   	double standardDeviation_pref = 0;
	   	int nOfEvaluations = 30;
	   	ArrayList<Double> results_utility = new ArrayList<Double>(nOfEvaluations);
	   	ArrayList<Double> results_score = new ArrayList<Double>(nOfEvaluations);
	   	ArrayList<Double> results_pref = new ArrayList<Double>();
	   	
	   	for (int i = 0; i < nOfEvaluations; i++) {
	   		problem.getPreferences().clear();
	   		problem.interact(0.5);	   		
	   		
	   		algorithm.execute();	   		
	   		
	   		Solution s = algorithm.getBestInteractiveSolution();
	   		results_utility.add(-s.getObjective(0));
	   		results_score.add(problem.calculateFitness(s));
	   		results_pref.add((double)problem.getPreferences().getNumberOfAttendedPref(s));
		}
	   	
	   	average_score = new Results().getAverage(results_score);
	   	average_utility = new Results().getAverage(results_utility);
	   	average_pref = new Results().getAverage(results_pref);
	   	
	   	standardDeviation_pref = new Results().getStandardDeviation(average_pref, results_pref);
	   	standardDeviation_score = new Results().getStandardDeviation(average_score, results_score);
	   	standardDeviation_utility = new Results().getStandardDeviation(average_utility, results_utility);
	   	
	   	System.out.println(
	   			average_pref+"+/-"+standardDeviation_pref+" "+
	   				average_score+"+/-"+standardDeviation_score+" "+
	   					average_utility+"+/-"+standardDeviation_utility);
	   	
	}
	
	public static void loadProperties(){
		Properties properties = new Properties();
		
		try {
			String basename = FilenameUtils.getBaseName(filename);
			properties.load(new FileInputStream("conf/" + basename + ".config"));
			
			populationSize = Integer.valueOf(properties.getProperty("populationSize",String.valueOf(IGA.POPULATION_SIZE)));
			maxGenerations = Integer.valueOf(properties.getProperty("maxGenerations", String.valueOf(IGA.MAX_GENERATIONS)));
			elitismRate = Double.valueOf(properties.getProperty("elitismRate", String.valueOf(IGA.ELITISM_RATE)));
			nGens = Integer.valueOf(properties.getProperty("nGens", String.valueOf(IGA.N_GENS)));
			nFeedback = Integer.valueOf(properties.getProperty("nFeedback", String.valueOf(IGA.N_FEEDBACK)));
			nIteractions = Integer.valueOf(properties.getProperty("nIteractions", String.valueOf(IGA.N_ITERACTIONS)));
			
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
