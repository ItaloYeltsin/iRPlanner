package jmetal.print.results;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;

public class PrintBestSolution extends PrintStats {

	@Override
	public void print(SolutionSet set, Problem problem) throws JMException {
		System.out.println("----------------------------");
		
		ReleasePlanningProblem p = (ReleasePlanningProblem) problem;
		Solution s = set.get(0);
		
		for (int i = 1; i <= p.getReleases(); i++) {
			System.out.print("Release "+i+": ");
			for (int j = 0; j < p.getRequirements(); j++) {
				if(s.getDecisionVariables()[j].getValue() == i){
					System.out.print(j);
					if(j+1 != p.getRequirements()){
						System.out.print(", ");
					}
				}				
			}
			
			System.out.println();
		}
		
		//Requirements out of releases
		System.out.print("Out: ");
		for (int j = 0; j < p.getRequirements(); j++) {
			if(s.getDecisionVariables()[j].getValue() == 0){
				System.out.print(j);
				if(j+1 != p.getRequirements()){
					System.out.print(", ");
				}
			}				
		}
	}
	
	

}
