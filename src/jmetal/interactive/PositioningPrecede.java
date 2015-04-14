package jmetal.interactive;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.util.JMException;

public class PositioningPrecede extends Preference{
	private int r1;
	private int r2;
	private int distance;

	PositioningPrecede(String args, int weight) {
		super(args, weight);
		String [] aux = args.split(" ");
		
		//Verify arguments consistency 
		if(aux.length != 3) 
			throw new IllegalArgumentException("Arguments don't match the correct format");
		
		r1 = Integer.parseInt(aux[0]);
		r2 = Integer.parseInt(aux[1]);
		distance = Integer.parseInt(aux[2]);
		setLogicalExpression(args);
	}

	@Override
	int evaluate(Solution solution) throws JMException {
		Variable [] variables = solution.getDecisionVariables();
		if((variables[r2].getValue() - variables[r1].getValue() >= distance)
				|| (variables[r2].getValue() == 0 && variables[r1].getValue() > 0)) {
			return 1;
		}
			
		return 0;
	}

	@Override
	public void setLogicalExpression(String args) {
		String [] aux = args.split(" ");
		int r1 = Integer.parseInt(aux[0]);
		int r2 = Integer.parseInt(aux[1]);
		int distance = Integer.parseInt(aux[2]);
		super.logicalExpression = "positioning_precede("+r1+", "+r2+", "+distance+")";
		
	}
}
