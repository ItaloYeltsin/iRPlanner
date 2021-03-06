package jmetal.interactive.preferences;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.interactive.core.Preference;
import jmetal.util.JMException;

public class PositioningAfter extends Preference{
	private int r1;
	private int k;

	public PositioningAfter(String args, int weight) {
		super(args, weight);
		String [] aux = args.split(" ");
		
		//Verify arguments consistency 
		if(aux.length != 2) 
			throw new IllegalArgumentException("Arguments don't match the correct format");
		
		r1 = Integer.parseInt(aux[0]);
		k = Integer.parseInt(aux[1]);
		setLogicalExpression(args);
	}

	@Override
	public double evaluate(Solution solution) throws JMException {
		Variable [] variables = solution.getDecisionVariables();
		if(variables[r1].getValue() - k >= 1
				|| (variables[r1].getValue() == 0)) {
			return 1.0;
		}
			
		return 0.0;
	}
	
	@Override
	public void setLogicalExpression(String args) {
		String [] aux = args.split(" ");
		int r1 = Integer.parseInt(aux[0]);
		int k = Integer.parseInt(aux[1]);
		super.logicalExpression = "positioning_after("+r1+", "+k+")";
		
	}
}
