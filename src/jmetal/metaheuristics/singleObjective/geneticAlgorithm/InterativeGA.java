package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
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

public class InterativeGA extends Algorithm {

	/**
	 * 
	 * Constructor Create a new IGA instance.
	 * 
	 * @param problem
	 *            Problem to solve.
	 */
	public InterativeGA(Problem problem) {
		super(problem);
	} // GGA

	/**
	 * Execute the IGA algorithm
	 * 
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int populationSize		;
		int maxGenerations		;
		int elitismRate			;
		int generation = 0		;
		int feedBackPeriod		;
		int numberOfFeedBacks	;
		int feedBackGeneration	;

		SolutionSet population;
		SolutionSet offspringPopulation;

		Operator mutationOperator;
		Operator crossoverOperator;
		Operator selectionOperator;

		Comparator comparator;

		ReleasePlanningProblem rpp = (ReleasePlanningProblem) problem_;
		comparator = new ObjectiveComparator(0); // Single objective comparator

		// Read the params
		populationSize = ((Integer) this.getInputParameter("populationSize"))
				.intValue();
		maxGenerations = ((Integer) this.getInputParameter("maxGenerations"))
				.intValue();

		rpp.setAlpha((Integer) this.getInputParameter("alpha")); //Set the alpha weight

		elitismRate = (int) ((double) populationSize * ((double) this
				.getInputParameter("elitismRate")));

		feedBackPeriod = (int) (this.getInputParameter("feedBackPeriod"));
		
		//Number Of FeedBack's per period
		numberOfFeedBacks = (int) (this.getInputParameter("numberOfFeedBacks"));

		feedBackGeneration = (int) (this
				.getInputParameter("feedBackGeneration"));
		
		if(feedBackGeneration > maxGenerations){
			throw new IllegalArgumentException("feedBackGeneration number must be less or equal than maxGenerations");
		}

		// Initialize the variables
		population = new SolutionSet(populationSize);
		offspringPopulation = new SolutionSet(populationSize);

		// Read the operators
		mutationOperator = this.operators_.get("mutation");
		crossoverOperator = this.operators_.get("crossover");
		selectionOperator = this.operators_.get("selection");

		// Create the initial population
		Solution newIndividual;
		for (int i = 0; i < populationSize; i++) {

			newIndividual = new Solution(problem_);
			repairSolution(newIndividual);
			problem_.evaluate(newIndividual);

			population.add(newIndividual);
		} // for

		// Sort population
		population.sort(comparator);
		while (generation < maxGenerations) {
			generation++;
			System.out.println(generation + ": "
					+ population.get(0).getObjective(0));

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

			// FeedBack
			if (generation % (int) (feedBackGeneration / feedBackPeriod) == 0
					&& generation <= feedBackGeneration) {
				for (int i = 0; i < numberOfFeedBacks; i++) {
					rpp.addConstraint(askForFeedBack());
				}
			}

			// The offspring population becomes the new current population
			population.clear();
			for (int i = 0; i < populationSize; i++) {
				problem_.evaluate(offspringPopulation.get(i));
				population.add(offspringPopulation.get(i));
			}
			offspringPopulation.clear();
			population.sort(comparator);

		} // while

		// Return a population with the best individual
		SolutionSet resultPopulation = new SolutionSet(1);
		resultPopulation.add(population.get(0));

		return resultPopulation;
	} // execute
	/**
	 * Ask for a feedback from the program user
	 * @return A vector with size 3 and {r1, r2, 0/1}
	 */
	private int[] askForFeedBack() {
		int[] aux = new int[3];
		Random random = new Random();
		int r1, r2;
		ReleasePlanningProblem rpp = (ReleasePlanningProblem) problem_;
		int range = (int) rpp.getNumberOfVariables();
		do {

			r1 = random.nextInt(range);
			r2 = random.nextInt(range);
		} while (rpp.IsConstraintAlreadySet(r1, r2) || r1 == r2);
		aux[0] = r1;
		aux[1] = r2;

		System.out.println("Should " + r1 + " and " + r2
				+ " be together(1) or separeted(0)?");
		do {
			Scanner read = new Scanner(System.in);
			aux[2] = read.nextInt();
		} while (aux[2] < 0 || aux[2] > 1);
		return aux;
	}

	/**
	 * Repair a solution that breaks the bound of some release
	 * @param A Solution
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
						
						double simulatedCost = getRequirementCost(listOfRequirements[j]) + releasesCost[orderIndices[k] - 1];
						if (simulatedCost <= getBudget(orderIndices[k])) {
							releasesCost[index - 1] -= getRequirementCost(listOfRequirements[j]);
							releasesCost[orderIndices[k] - 1] = simulatedCost;
							individual[listOfRequirements[j]].setValue(orderIndices[k]);
							wasChanged = true;
							break;
						}

					}
					if(!wasChanged){
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
		for (Iterator iterator = p.iterator(); iterator.hasNext();) {
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
} // IGA