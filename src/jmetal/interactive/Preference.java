package jmetal.interactive;

import jmetal.core.Solution;
import jmetal.util.JMException;

public abstract class Preference {
	protected String logicalExpression;
	/**
	 * Parameters
	 */
	private String args;
	/**
	 * Weight Value
	 */
	private int weight;
	/**
	 * Constructor
	 * @param args
	 * @param weight
	 */
	Preference(String args, int weight) {
		this.args = args;
		this.weight = weight;
	}
	/**
	 * Evaluate if an specific solution obey the defined rule
	 * @param solution
	 * @return
	 * @throws JMException 
	 */
	abstract int evaluate(Solution solution) throws JMException;
	/**
	 * 
	 * @param weight
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	/**
	 * 
	 * @return weight
	 */
	public int getWeight() {
		return weight;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return logicalExpression;
	}
	
	public abstract void setLogicalExpression(String args);	
	
}
