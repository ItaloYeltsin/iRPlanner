package jmetal.interactive;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.util.JMException;

public class CouplingDisjoint extends Preference{
	int r1;
	int r2;
	CouplingDisjoint(String args, int weight) {
		super(args, weight);
		String [] aux = args.split(" ");
		
		//Verify arguments consistency 
				if(aux.length != 2) 
					throw new IllegalArgumentException("Arguments don't match the correct format");
				
		r1 = Integer.parseInt(aux[0]);
		r2 = Integer.parseInt(aux[1]);
		setLogicalExpression(args);
	}
	@Override
	int evaluate(Solution solution) throws JMException {
		Variable [] variable = solution.getDecisionVariables();
		if (variable[r1].getValue() != variable[r2].getValue()) {
			return 1;
		}
		
		return 0;
	}
	
	@Override
	public void setLogicalExpression(String args) {
		String [] aux = args.split(" ");
		int r1 = Integer.parseInt(aux[0]);
		int r2 = Integer.parseInt(aux[1]);
		super.logicalExpression = "coupling_disjoint("+r1+", "+r2+")";
		
	}

}
