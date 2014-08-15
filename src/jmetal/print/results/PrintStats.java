package jmetal.print.results;

import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;

public abstract class PrintStats {
	
	public abstract void print(SolutionSet set, Problem problem) throws JMException;

}
