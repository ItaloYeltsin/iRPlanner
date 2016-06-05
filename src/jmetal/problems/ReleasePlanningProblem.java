package jmetal.problems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.interactive.core.PreferencesBase;
import jmetal.interactive.management.InteractionManagement;
import jmetal.interactive.preferences.simulator.Simulator;
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
public class ReleasePlanningProblem extends Problem {

	protected int[] risk;

	protected int[] cost;

	protected int[] satisfaction;

	protected int[][] customerSatisfaction;

	protected int[] customerImportance;

	protected int requirements;

	protected int customers;

	protected Integer [] releaseCost;

	protected InstanceReader reader;

	private String filename;

	private String simulator;

	private String scenario;

	private int[][] precedence;

	private double alpha = 1; // feedback weight

	private PreferencesBase preferences;

	private ArrayList<HashMap> preferenceList;

	private String[] reqDescriptions;

	public PreferencesBase getPreferences() {
		return preferences;
	}

	private InteractionManagement mngmnt;

	public String getFilename() {
		return filename;
	}

	public String getScenario() {
		return scenario;
	}

	public void setSimulator(String simulator) {
		this.simulator = simulator;

	}

	public ReleasePlanningProblem(String filename) throws IOException {
		this.filename = filename;
		this.reader = new InstanceReader(filename);

		reader.open();

		readParameters();
		readCustomerImportance();
		readRiskAndCost();
		readCustomerSatisfaction();
		//readReleaseCost();
		releaseCost = new Integer[1];
		releaseCost[0] = 0;
		precedence = reader.readIntMatrix(requirements, requirements, " ");
		readDescriptions();
		reader.close();

		problemName_ = "ReleasePlanningProblem";
		numberOfVariables_ = getRequirements();
		numberOfObjectives_ = 3; // in fact the problem has a mono-objective
									// formulation, however, extra objectives
									// are used to store value of other metrics
		numberOfConstraints_ = 1;

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int i = 0; i < numberOfVariables_; i++) {
			upperLimit_[i] = 0;
			lowerLimit_[i] = 0;
		}
		
		try {
			solutionType_ = new IntSolutionType(this);
		} catch (Exception e) {
			System.out.println(e);
		}

		preferences = new PreferencesBase();
	}

	private void readDescriptions() {
		reqDescriptions = new String[requirements];

		for (int i = 0; i < reqDescriptions.length; i++) {
			reqDescriptions[i] = reader.readLine();
		}

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
		this.releaseCost = reader.readIntegerVector(" ");
	}

	private void readCustomerSatisfaction() {
		this.satisfaction = new int[requirements];
		this.customerSatisfaction = reader.readIntMatrix(customers,
				requirements, " ");

		for (int i = 0; i < requirements; i++) {
			for (int j = 0; j < customers; j++) {
				satisfaction[i] += customerImportance[j]
						* customerSatisfaction[j][i];
			}
		}
	}

	
	private void readParameters() {
		int[] params = reader.readIntVector(" ");

		this.requirements = params[0];
		this.customers = params[1];
	}

	private void readCustomerImportance() {
		this.customerImportance = reader.readIntVector(" ");
	}

	/**
	 * Return number of Releases
	 * 
	 * @return number of Releases
	 */
	public int getReleases() {
		return releaseCost.length;
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
	 * @param i
	 *            Requirement ID
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
	 * @param i
	 *            Requirement ID
	 * @return The Requirement Risk
	 */
	public int getRisk(int i) {
		if (i < 0 || i + 1 > requirements) {
			throw new IllegalArgumentException("requirement id not found");
		}

		return risk[i];
	}
	
	public int[][] getCustomerSatisfaction() {
		return customerSatisfaction;
	}
	
	public int[] getCustomerImportance() {
		return customerImportance;
	}

	/**
	 * 
	 * @param solution
	 * @return
	 * @throws JMException
	 */
	public double getRisk(Solution solution) throws JMException {
		double sumRisks = 0;
		Variable[] variables = solution.getDecisionVariables();
		for (int i = 0; i < variables.length; i++) {
			try {
				sumRisks += getRisk(i) * (Double) variables[i].getValue();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sumRisks;
	}

	public double getImportance(Solution solution) throws JMException {
		double sumImportances = 0;
		Variable[] variables = solution.getDecisionVariables();
		for (int i = 0; i < variables.length; i++) {
			sumImportances = (double) satisfaction[i]
					* (releaseCost.length - (Double) variables[i].getValue() + 1);
		}
		return sumImportances;
	}
	
	public int[][] getPrecedence() {
		return precedence;
	}
	
	public int[] getSatisfaction() {
		return satisfaction;
	}

	/**
	 * Return the Requirement Cost
	 * 
	 * @param i
	 *            Requirement ID
	 * @return The Requirement Cost
	 */
	public int getCost(int i) {
		if (i < 0 || i + 1 > requirements) {
			throw new IllegalArgumentException("requirement id not found" + i);
		}

		return cost[i];
	}

	/**
	 * Return the release cost
	 * 
	 * @param i
	 *            Release ID
	 * @return The Release Cost
	 */
	public int getBudget(int i) {
		if (i < 0 || i > releaseCost.length) {
			throw new IllegalArgumentException("release id not found " + i);
		}

		return releaseCost[i - 1];
	}

	/**
	 * Set alpha
	 * 
	 * @param alpha
	 */
	public void setAlpha(double alpha) {
		if(alpha < 0)
			throw new IllegalArgumentException("Invalid weight configuration.");
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
		Variable[] individual = solution.getDecisionVariables();

		for (int i = 0; i < getRequirements(); i++) {
			int gene = (int) individual[i].getValue();
			if (gene == 0)
				continue;

			solutionScore += (double) satisfaction[i]
					* (releaseCost.length - gene + 1) - getRisk(i) * gene;

		}
		return solutionScore;
	}

	@Override
	public void evaluate(Solution solution) throws JMException {
		double solutionScore = 0;
		solutionScore = calculateFitness(solution);
		double utility = solutionScore
				/ (1.0 + (alpha * (double) preferences.evaluate(solution)));

		solution.setObjective(0, -utility / (1 + evaluatePrecedences(solution))); // objective
																					// of
																					// the
																					// formulation
		solution.setObjective(1, -solutionScore); // score metric

		// SL metric
		if (preferences.getWeightSumOfAllPref() != 0)
			solution.setObjective(2,
					-preferences.getWeightSumOfSatisfiedPref(solution)
							/ preferences.getWeightSumOfAllPref());
		else
			solution.setObjective(2, 0);
	}

	/**
	 * 
	 * @param solution
	 * @return the number of broken precedence constraints of a solution
	 * @throws JMException
	 */
	public int evaluatePrecedences(Solution solution) throws JMException {
		Variable[] variables = solution.getDecisionVariables();
		int counter = 0;
		for (int i = 0; i < variables.length; i++) {
			if (variables[i].getValue() != 0) {
				for (int j = 0; j < variables.length; j++) {
					if (precedence[i][j] == 1
							&& variables[j].getValue() > variables[i]
									.getValue())
						counter++;
				}
			}
		}
		return counter;
	}

	public void interact() throws IOException {
		mngmnt.mainMenu();
	}

	public void interact(double rate) throws IOException {
		if (rate < 0.0 || rate > 1.0)
			throw new IllegalArgumentException("Rate: " + rate
					+ " Out of bounds!");
		preferenceList = new Simulator(simulator)
				.getUserPreferences((int) (rate * (double) numberOfVariables_));
		int count = 0;
		for (HashMap p : preferenceList) {
			preferences.add((String) p.get("type"), (String) p.get("args"),
					Integer.parseInt((String) p.get("level")));

		}

	}

	public boolean exitMenu() {
		return mngmnt.exitMenu();
	}

	public String[] getReqDescriptions() {
		return reqDescriptions;
	}

	public void setReqDescriptions(String[] reqDescriptions) {
		this.reqDescriptions = reqDescriptions;
	}

	public Integer[] getReleaseCost() {
		return releaseCost;
	}

	public void setReleaseCost(Integer[] releaseCost) {
		this.releaseCost = releaseCost;
		//update upperLimit
		for (int i = 0; i < numberOfVariables_; i++) {
			upperLimit_[i] = releaseCost.length;
		}
	}

	public double getAlpha() {
		return alpha;
	}

	

} // ReleasePlanningProblem