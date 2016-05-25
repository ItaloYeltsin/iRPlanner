package jmetal.metaheuristics.iga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.print.results.PrintBestSolution;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;
import jmetal.util.comparators.ObjectiveComparator;

/**
 * The Release Planning Problem Class
 * 
 * @author Italo Yeltsin
 * @since 2014-08-01
 * @version 1.0
 * 
 */

public class IGA extends Algorithm {

	private static final boolean DEBUG_SHOW_CURRENT_BEST_SOLUTION = false;

	private static final long serialVersionUID = 1L;

	public static final int POPULATION_SIZE = 100;

	public static final int MAX_GENERATIONS = 200;

	public static final double ELITISM_RATE = 0.2;

	public static final int N_GENS = 200;

	public static final int N_FEEDBACK = 2;

	public static final int N_ITERACTIONS = 5;

	protected Random random = new Random(System.currentTimeMillis());

	protected int generation = 0;

	protected SolutionSet population;
	protected SolutionSet offspringPopulation;

	protected Operator mutationOperator;
	protected Operator crossoverOperator;
	protected Operator selectionOperator;

	@SuppressWarnings("rawtypes")
	protected Comparator comparator;

	protected int populationSize = POPULATION_SIZE;
	protected double elitismRate = ELITISM_RATE * populationSize;
	protected int maxGenerations = MAX_GENERATIONS;
	protected int nGens = N_GENS;
	protected int nFeedback = N_FEEDBACK;
	protected int nIteractions = N_ITERACTIONS;

	private Solution nonInteractiveSolution;

	private Solution interactiveSolution;

	/**
	 * 
	 * Constructor Create a new IGA instance.
	 * 
	 * @param problem
	 *            Problem to solve.
	 */
	public IGA(Problem problem) {
		super(problem);
		
		/* Algorithm parameters */
		setInputParameter("populationSize", IGA.POPULATION_SIZE);
		setInputParameter("elitismRate", IGA.ELITISM_RATE);
		setInputParameter("nGens", IGA.N_GENS);
		setInputParameter("crossoverRate", 0.9);
		setInputParameter("mutationRate", 0.01);

		

	} // GGA

	/**
	 * Execute the IGA algorithm
	 * 
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		ReleasePlanningProblem rpp = (ReleasePlanningProblem) problem_;
		
		// Operators
				try {
					addOperator("crossover", getCrossoverOperator());
				} catch (JMException e) {
					e.printStackTrace();
				}
				try {
					addOperator("mutation", getMutationOperator());
				} catch (JMException e) {
					e.printStackTrace();
				}
				try {
					addOperator("selection", getSelectionOperator());
				} catch (JMException e) {
					e.printStackTrace();
				}
		
		comparator = new ObjectiveComparator(0); // Single objective comparator

		// Read the params
		populationSize = ((Integer) this.getInputParameter("populationSize"))
				.intValue();
		//maxGenerations = ((Integer) this.getInputParameter("maxGenerations"))
		//		.intValue();
		elitismRate = (int) ((double) populationSize * ((double) this
				.getInputParameter("elitismRate")));
		nGens = (int) (this.getInputParameter("nGens"));
		//nFeedback = (int) (this.getInputParameter("nFeedback"));
		//nIteractions = (int) (this.getInputParameter("nIteractions"));
		// Initialize the variables
		population = new SolutionSet(populationSize);
		offspringPopulation = new SolutionSet(populationSize);

		// Read the operators
		mutationOperator = this.operators_.get("mutation");
		crossoverOperator = this.operators_.get("crossover");
		selectionOperator = this.operators_.get("selection");
		
		/*
		 * Non-Interactive Approach
		 
		
		System.out.println("------Non-Interactive Approach-------");
		createInitialPopulation();
		executeBy(nGens);
		new PrintBestSolution().print(population, problem_);
		System.out.println("--------------------------------------");
		nonInteractiveSolution = population.get(0);
		
		 * Interactive Approach
		 */

		//boolean exit = false;

		//while (!exit) {
			//population.clear();
			/*if(interactiveSolution != null)
				population.add(interactiveSolution);
			else{
				Solution newIndividual = new Solution(problem_);
				repairSolution(newIndividual);
				problem_.evaluate(newIndividual);

				population.add(newIndividual);
			}*/
			// Interaction
			/*try {
				System.out.println("2");
				rpp.interact();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			// Execute GA by nGens
			createInitialPopulation();
			executeBy(nGens);
			//new PrintBestSolution().print(population, problem_);
			interactiveSolution = population.get(0);
			
			//exit = rpp.exitMenu();
		//}

		SolutionSet resultPopulation = new SolutionSet(1);
		resultPopulation.add(population.get(0));
		
		return resultPopulation;
	}

	private void createInitialPopulation() throws ClassNotFoundException,
			JMException {
		for (int i = 0; i < populationSize; i++) {
			Solution newIndividual = new Solution(problem_);
			repairSolution(newIndividual);
			problem_.evaluate(newIndividual);

			population.add(newIndividual);
		}
	
		// Sort population
		population.sort(comparator);
	}

	public void executeOneGeneration() throws JMException {
		generation++;

		// Copy the best individuals to the offspring population
		for (int i = 0; i < elitismRate; i++) {
			offspringPopulation.add(new Solution(population.get(i)));
		}

		Solution[] offspring;
		// Reproductive cycle
		for (int i = 0; i < (populationSize / 2 - elitismRate / 2); i++) {
			// Selection

			Solution[] parents = new Solution[2];

			parents[0] = (Solution) selectionOperator.execute(population);
			parents[1] = (Solution) selectionOperator.execute(population);

			// Crossover
			offspring = (Solution[]) crossoverOperator.execute(parents);

			// Mutation
			mutationOperator.execute(offspring[0]);
			mutationOperator.execute(offspring[1]);

			// Repair Invalid Individual
			repairSolution(offspring[0]);
			repairSolution(offspring[1]);

			// Replacement: the two new individuals are inserted in the
			// offspring
			// population
			offspringPopulation.add(offspring[0]);
			offspringPopulation.add(offspring[1]);

		} // for

		// The offspring population becomes the new current population
		population.clear();

		for (int i = 0; i < populationSize; i++) {
			problem_.evaluate(offspringPopulation.get(i));
			population.add(offspringPopulation.get(i));
		}
		offspringPopulation.clear();
		population.sort(comparator);
		
		if (DEBUG_SHOW_CURRENT_BEST_SOLUTION) {
			System.out.println("Generation: " + generation);
			System.out.println("\tBest Value: " + population.get(0).toString());
		}
	}

	public void executeBy(int nGens) throws JMException {
		Solution bestIndividual = population.get(0);
		int count = 0;
		while (count < nGens) {
			
			executeOneGeneration();
			if(-(Double)bestIndividual.getObjective(0) < -(Double)population.get(0).getObjective(0)){
				bestIndividual = population.get(0);			
				count = 0;
			}
			else
				count++;
		}
	}

	/**
	 * Repair a solution that breaks the bound of some release
	 * 
	 * @param A
	 *            Solution
	 */
	public void repairSolution(Solution solution) throws JMException {

		Variable[] individual = solution.getDecisionVariables();
		int[] indices = new int[(int) problem_.getUpperLimit(0)];
		int[] orderIndices = new int[(int) problem_.getUpperLimit(0)];
		double[] releasesCost = new double[(int) problem_.getUpperLimit(0)];

		for (int i = 0; i < releasesCost.length; i++) {
			indices[i] = i + 1;
			orderIndices[i] = i + 1;
			releasesCost[i] = calculateReleaseCost(solution, i + 1);
		}

		suffle(indices);

		for (int i = 0; i < releasesCost.length; i++) {
			int index = indices[i];

			if (releasesCost[index - 1] > getBudget(index)) {
				// Begining of Repair
				int[] listOfRequirements = getSetOfRequirements(index, solution);
				suffle(listOfRequirements);
				suffle(orderIndices);

				for (int j = 0; (j < listOfRequirements.length && releasesCost[index - 1] > getBudget(index)); j++) {
					boolean wasChanged = false;

					for (int k = 0; k < orderIndices.length; k++) {

						double simulatedCost = getRequirementCost(listOfRequirements[j])
								+ releasesCost[orderIndices[k] - 1];
						if (simulatedCost <= getBudget(orderIndices[k])) {
							releasesCost[index - 1] -= getRequirementCost(listOfRequirements[j]);
							releasesCost[orderIndices[k] - 1] = simulatedCost;
							individual[listOfRequirements[j]]
									.setValue(orderIndices[k]);
							wasChanged = true;
							break;
						}

					}
					if (!wasChanged) {
						releasesCost[index - 1] -= getRequirementCost(listOfRequirements[j]);
						individual[listOfRequirements[j]].setValue(0);
					}
				}
			}
		}

	} // repairSolution

	/**
	 * 
	 * @param release
	 * @param solution
	 * @return A vector with the Set of Requirements of a given Release
	 * @throws JMException
	 */
	private int[] getSetOfRequirements(int release, Solution solution)
			throws JMException {
		ArrayList<Integer> p = new ArrayList<Integer>();
		Variable[] individual = solution.getDecisionVariables();
		int[] vet;
		for (int i = 0; i < individual.length; i++) {
			if ((int) individual[i].getValue() == release) {
				p.add(i);
			}
		}
		vet = new int[p.size()];

		int count = 0;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = p.iterator(); iterator.hasNext();) {
			Integer integer = (Integer) iterator.next();
			vet[count++] = integer;
		}
		return vet;
	}

	/**
	 * 
	 * @param solution
	 * @param release
	 * @return The cost of a given release in a given solution
	 * @throws JMException
	 */
	public double calculateReleaseCost(Solution solution, int release)
			throws JMException {
		double cost = 0.0;
		Variable[] individual = solution.getDecisionVariables();

		for (int i = 0; i < individual.length; i++) {
			if ((int) individual[i].getValue() == release) {
				cost += getRequirementCost(i);
			}
		}

		return cost;
	}

	/**
	 * @param Requirement
	 *            ID
	 * @return Requirement Cost
	 */
	private double getRequirementCost(int i) {
		ReleasePlanningProblem p = (ReleasePlanningProblem) problem_;
		return p.getCost(i);
	}

	/**
	 * 
	 * @param Release
	 *            ID
	 * @return The Budget of a given release
	 */
	private double getBudget(int i) {
		ReleasePlanningProblem p = (ReleasePlanningProblem) problem_;
		return p.getBudget(i);
	}

	/**
	 * @param vet
	 */
	private void suffle(int[] vet) {
		Random random = new Random();
		for (int i = 0; i < vet.length; i++) {

			int pos = random.nextInt(vet.length);
			int aux = vet[i];
			vet[i] = vet[pos];
			vet[pos] = aux;

		}
	}
	
	public Solution getBestNonInteractiveSolution() {
		return nonInteractiveSolution;
	}
	
	public Solution getBestInteractiveSolution() {
		return interactiveSolution;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Operator getMutationOperator() throws JMException {
		HashMap parameters = new HashMap();
		parameters.put("probability", getInputParameter("mutationRate"));
		return MutationFactory.getMutationOperator("BitFlipMutation",
				parameters);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Operator getCrossoverOperator() throws JMException {
		HashMap parameters = new HashMap();
		parameters.put("probability", getInputParameter("crossoverRate"));
		return CrossoverFactory.getCrossoverOperator(
				"SinglePointCrossover", parameters);
	}

	public Operator getSelectionOperator() throws JMException {
		return SelectionFactory.getSelectionOperator(
				"BinaryTournament", null);
	}
} // IGA