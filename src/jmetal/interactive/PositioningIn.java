package jmetal.interactive;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.util.JMException;

public class PositioningIn extends Preference{
	private int r1;
	private int k;
	PositioningIn(String args, int weight) {
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
	int evaluate(Solution solution) throws JMException {
		Variable [] variables = solution.getDecisionVariables();
		
		if(variables[r1].getValue() == k) {
			return 1;
		}
			
		return 0;
	}
	
	@Override
	public void setLogicalExpression(String args) {
		String [] aux = args.split(" ");
		int r1 = Integer.parseInt(aux[0]);
		int k = Integer.parseInt(aux[1]);
		
		super.logicalExpression = "positioning_in("+r1+", "+k+")";
		
	}
}