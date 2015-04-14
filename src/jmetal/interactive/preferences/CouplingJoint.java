package jmetal.interactive.preferences;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.interactive.core.Preference;
import jmetal.util.JMException;

public class CouplingJoint extends Preference{
	int r1;
	int r2;
	public CouplingJoint(String args, int weight) {
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
	public int evaluate(Solution solution) throws JMException {
		Variable [] x = solution.getDecisionVariables();
		if(x[r1].getValue() == x[r2].getValue()) {
			return 1;
		}else {
			return 0;
		}
	}
	
	@Override
	public void setLogicalExpression(String args) {
		String [] aux = args.split(" ");
		int r1 = Integer.parseInt(aux[0]);
		int r2 = Integer.parseInt(aux[1]);
		super.logicalExpression = "coupling_joint("+r1+", "+r2+")";
		
	}
}
