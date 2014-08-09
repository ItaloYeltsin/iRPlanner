//  gGA.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

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
	 * Constructor Create a new GGA instance.
	 * 
	 * @param problem
	 *            Problem to solve.
	 */
	public InterativeGA(Problem problem) {
		super(problem);
	} // GGA

	/**
	 * Execute the GGA algorithm
	 * 
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int populationSize;
		int maxEvaluations;
		int evaluations;
		int elitismRate;
		SolutionSet population;
		SolutionSet offspringPopulation;

		Operator mutationOperator;
		Operator crossoverOperator;
		Operator selectionOperator;

		Comparator comparator;
		comparator = new ObjectiveComparator(0); // Single objective comparator

		// Read the params
		populationSize = ((Integer) this.getInputParameter("populationSize"))
				.intValue();
		maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations"))
				.intValue();
		elitismRate = (int) ((double)populationSize*((double) this.getInputParameter("elitismRate")));
		
		// Initialize the variables
		population = new SolutionSet(populationSize);
		offspringPopulation = new SolutionSet(populationSize);
		
		evaluations = 0;

		// Read the operators
		mutationOperator = this.operators_.get("mutation");
		crossoverOperator = this.operators_.get("crossover");
		selectionOperator = this.operators_.get("selection");

		// Create the initial population
		Solution newIndividual;
		for (int i = 0; i < populationSize; i++) {
			do {
				newIndividual = new Solution(problem_);
				problem_.evaluateConstraints(newIndividual);
			} while (newIndividual.isMarked());
			problem_.evaluate(newIndividual);
			
			evaluations++;
			population.add(newIndividual);
		} // for

		// Sort population
		population.sort(comparator);
		while (evaluations < populationSize*maxEvaluations) {
			if ((evaluations % 10) == 0) {
				System.out.println(evaluations + ": "
						+ population.get(0).getObjective(0));
			} //

			// Copy the best individuals to the offspring population
			for (int i = 0; i < elitismRate; i++) {
				offspringPopulation.add(new Solution(population.get(i)));
			}
			
			Solution[] offspring;
			// Reproductive cycle
			for (int i = 0; i < (populationSize / 2 - elitismRate/2); i++) {
				// Selection

				Solution[] parents = new Solution[2];

				parents[0] = (Solution) selectionOperator.execute(population);
				parents[1] = (Solution) selectionOperator.execute(population);

				// Crossover
				offspring = (Solution[]) crossoverOperator.execute(parents);

				// Mutation
				mutationOperator.execute(offspring[0]);
				mutationOperator.execute(offspring[1]);

				
								
				evaluations += 2;
				repairSolution (offspring[0]);
				repairSolution (offspring[1]);
				// Evaluation of the new individual
				problem_.evaluate(offspring[0]);
				problem_.evaluate(offspring[1]);
				// Replacement: the two new individuals are inserted in the
				// offspring
				// population
				offspringPopulation.add(offspring[0]);
				offspringPopulation.add(offspring[1]);
			} // for

			// The offspring population becomes the new current population
			population.clear();
			
			for (int i = 0; i < populationSize; i++) {
				population.
				add(offspringPopulation.get(i));
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
	 * @param A Solution
	 */
	private void repairSolution(Solution solution) throws JMException {
		
		Variable[] individual = solution.getDecisionVariables();
		int [] indices = new int [(int) problem_.getUpperLimit(0)];
		int [] orderIndices = new int [(int) problem_.getUpperLimit(0)];
		double [] releasesCost = new double [(int) problem_.getUpperLimit(0)];
		
		for (int i = 0; i < releasesCost.length; i++) {
			indices[i] = i+1;
			orderIndices[i] = i+1;
			releasesCost[i] = calculateReleaseCost(solution, i+1);
		}
		
		suffle(indices);
				
		for (int i = 0; i < releasesCost.length; i++) {
			int index = indices[i];
			if(releasesCost[index-1] > getBudget(index)) {
				int[] listOfRequirements = getSetOfRequirements(index, solution);
				suffle(listOfRequirements);
				suffle(orderIndices);
				for (int j = 0; (j < listOfRequirements.length && releasesCost[index-1] > getBudget(index)); j++) {
					 
					for (int k = 0; k < orderIndices.length; k++) {
					
						double simulatedCost = getRequirementCost(listOfRequirements[j]) + releasesCost[orderIndices[k]-1];
						if(simulatedCost <= getBudget(orderIndices[k])) {
							
							releasesCost[index-1] -= getRequirementCost(listOfRequirements[j]);
							releasesCost[orderIndices[k]-1] = simulatedCost;
							individual[listOfRequirements[j]].setValue(orderIndices[k]);
							break;
						
						}
												
					}
				}
			}
		}
		
	}
	/**
	 * 
	 * @param release
	 * @param solution
	 * @return A vector with the Set of Requirements of a given Release
	 * @throws JMException
	 */
	private int[] getSetOfRequirements(int release, Solution solution) throws JMException {
		ArrayList<Integer> p = new ArrayList<Integer>();
		Variable[] individual = solution.getDecisionVariables();
		int[] vet;
		for (int i = 0; i < individual.length; i++) {
			if((int)individual[i].getValue() == release) {
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
	private double calculateReleaseCost(Solution solution, int release) throws JMException {
		double cost = 0.0;
		Variable[] individual = solution.getDecisionVariables();
				
		for (int i = 0; i < individual.length; i++) {
			if((int)individual[i].getValue() == release) {
				cost += getRequirementCost(i);
			}
		}
		
		return cost;
	}
	/**
	 * @param Requirement ID 
	 * @return Requirement Cost
	 */
	private double getRequirementCost(int i) {
		ReleasePlanningProblem p = (ReleasePlanningProblem)problem_;
		return p.getCost(i);
	}
	/**
	 * 
	 * @param Release ID
	 * @return The Budget of a given release
	 */
	private double getBudget(int i) {
		ReleasePlanningProblem p = (ReleasePlanningProblem)problem_;
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
} // gGA