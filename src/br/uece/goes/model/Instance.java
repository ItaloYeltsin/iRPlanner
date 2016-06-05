package br.uece.goes.model;

public class Instance {
	
	private int nOfClients;
	private int nOfRequirements;
	private int [][] score;
	private int [] clientWeight;
	private int [][] precedence;
	private int [] cost;
	private int [] risk;
	private String [] descriptions;
	
	
	public String[] getDescriptions() {
		return descriptions;
	}


	public void setDescriptions(String[] descriptions) {
		this.descriptions = descriptions;
	}


	public Instance(int nOfClients, int nOfRequirements) {
		this.nOfClients = nOfClients;
		this.nOfRequirements = nOfRequirements;
	}


	public int getnOfClients() {
		return nOfClients;
	}


	public void setnOfClients(int nOfClients) {
		this.nOfClients = nOfClients;
	}


	public int getnOfRequirements() {
		return nOfRequirements;
	}


	public void setnOfRequirements(int nOfRequirements) {
		this.nOfRequirements = nOfRequirements;
	}


	public int[][] getScore() {
		return score;
	}


	public void setScore(int[][] score) {
		this.score = score;
	}


	public int[] getClientWeight() {
		return clientWeight;
	}


	public void setClientWeight(int[] clientWeight) {
		this.clientWeight = clientWeight;
	}


	public int[][] getPrecedence() {
		return precedence;
	}


	public void setPrecedence(int[][] precedence) {
		this.precedence = precedence;
	}


	public int[] getCost() {
		return cost;
	}


	public void setCost(int[] cost) {
		this.cost = cost;
	}


	public int[] getRisk() {
		return risk;
	}


	public void setRisk(int[] risk) {
		this.risk = risk;
	}
}
