package jmetal.problems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.interactive.PreferencesBase;
import jmetal.interactive.management.InteractionManagement;
import jmetal.util.InstanceReader;
import jmetal.util.JMException;



/**
 * The Release Planning Problem Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-30
 * @version 1.0
 *
 */
public class ReleasePlanningProblem extends Problem{

	protected int[] risk;
	
	protected int[] cost;
	
	protected int[] satisfaction;

	protected int[][] customerSatisfaction;
	
	protected int[] customerImportance;
	
	protected int releases;
	
	protected int requirements;
	
	protected int customers;
	
	protected int[] releaseCost;
	
	protected InstanceReader reader;
	
	private String filename;
	
	private ArrayList <int[]> constraints;
	
	private boolean[][] alreadySetConstraints;
	
	private double alpha; // feedback weight
	
	private PreferencesBase preferences; 
	
	private InteractionManagement mngmnt;
	
	public String getFilename() {
		return filename;
	}
		
	public ReleasePlanningProblem(String filename) {
		this.filename = filename;
		this.reader = new InstanceReader(filename);
			
		reader.open();
				
		readParameters();
		readCustomerImportance();
		readRiskAndCost();
		readCustomerSatisfaction();
		readReleaseCost();
		
		reader.close();		
		
		problemName_ = "ReleasePlanningProblem";
		numberOfVariables_ = getRequirements();
		numberOfObjectives_ = 1;
		numberOfConstraints_ = 1;
		
		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];
		
		for (int i = 0; i < numberOfVariables_; i++) {
			upperLimit_[i] = getReleases();
			lowerLimit_[i] = 0;
		}
		
		try {
			solutionType_ = new IntSolutionType(this);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		preferences = new PreferencesBase();
		mngmnt = new InteractionManagement(preferences);
	}

	private void readRiskAndCost() {
		this.risk = new int[requirements];
		this.cost = new int[requirements];
		
		int[][] info = reader.readIntMatrix(requirements, 2, " ");

		for (int i = 0; i < requirements; i++) {
			this.cost[i] = info[i][0];
			this.risk[i] = info[i][1];
		}
	}

	private void readReleaseCost() {
		this.releaseCost = reader.readIntVector(" ");
	}

	private void readCustomerSatisfaction() {
		this.satisfaction = new int[requirements];
		this.customerSatisfaction = reader.readIntMatrix(customers, requirements, " ");
		
		for (int i = 0; i < requirements; i++) {
			for (int j = 0; j < customers; j++) {
				satisfaction[i] += customerImportance[j] * customerSatisfaction[j][i];
			}
		}
	}
	
	private void readParameters(){
		int[] params = reader.readIntVector(" ");
		
		this.releases = params[0];
		this.requirements = params[1];
		this.customers = params[2];
	}
	
	private void readCustomerImportance(){
		this.customerImportance = reader.readIntVector(" ");
	}

	/**
	 * Return number of Releases
	 * 
	 * @return number of Releases
	 */
	public int getReleases() {
		return releases;
	}

	/**
	 * Return number of Requirements
	 * 
	 * @return number of Requirements
	 */
	public int getRequirements() {
		return requirements;
	}

	/**
	 * Return number of customers
	 * 
	 * @return number of customers
	 */
	public int getCustomers() {
		return customers;
	}
	
	/**
	 * Return sum all requirement score
	 * 
	 * @return all release scores
	 */
	public int getSumRequirementScores() {
		int value = 0;

		for (int i = 0; i < requirements; i++) {
			value += satisfaction[i];
		}

		return value;
	}
	
	/**
	 * Get sum all requirement cost
	 * 
	 * @return all cost
	 */
	public int getCost() {
		int value = 0;

		for (int i = 0; i < requirements; i++) {
			value += cost[i];
		}

		return value;
	}
	
	/**
	 * Return the Requirement Score
	 * 
	 * @param i Requirement ID
	 * @return The Requirement Score
	 */
	public int getRequirementScore(int i) {
		if (i < 0 || i + 1 > requirements) {
			throw new IllegalArgumentException("requirement id not found");
		}

		return satisfaction[i];
	}

	/**
	 * Return the Requirement Risk
	 * 
	 * @param i Requirement ID
	 * @return The Requirement Risk
	 */
	public int getRisk(int i) {
		if (i < 0 || i + 1 > requirements) {
			throw new IllegalArgumentException("requirement id not found");
		}
		
		return risk[i];
	}
	
	/**
	 * Return the Requirement Cost
	 * 
	 * @param i Requirement ID
	 * @return The Requirement Cost
	 */	
	public int getCost(int i){
		if (i < 0 || i + 1 > requirements) {
			throw new IllegalArgumentException("requirement id not found" + i);
		}
		
		return cost[i];
	}
	
	/**
	 * Return the release cost
	 * 
	 * @param i Release ID
	 * @return The Release Cost
	 */
	public int getBudget(int i){
		if (i < 0 || i > releases) {
			throw new IllegalArgumentException("release id not found" + i);
		}
		
		return releaseCost[i-1];
	}
	/**
	 * Set alpha 
	 * @param alpha
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	/**
	 * 
	 * @param solution
	 * @return The fitness value of a given solution
	 * @throws JMException
	 */
	public double calculateFitness(Solution solution) throws JMException {
		double solutionScore = 0;
		Variable [] individual = solution.getDecisionVariables();
		
		for (int i = 0; i < getRequirements(); i++) {
			int gene = (int) individual[i].getValue();
			if(gene == 0) continue;
			
			solutionScore += (double)satisfaction[i]*(getReleases() - gene + 1) - getRisk(i)*gene;
	
		}
		return solutionScore;
	}
	@Override
	public void evaluate(Solution solution) throws JMException {
		double solutionScore = 0;
		solutionScore = calculateFitness(solution);
		solutionScore = solutionScore/(1+ alpha*preferences.evaluate(solution));
			
		solution.setObjective(0, -solutionScore);
	}

	public void interact() throws IOException {
		mngmnt.mainMenu();
	}

	
} // ReleasePlanningProblem