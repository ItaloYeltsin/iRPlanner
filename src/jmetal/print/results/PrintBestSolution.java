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
		
		p.getPreferences().listPreferences(s);
		
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
		System.out.print("Not Allocated: ");
		for (int j = 0; j < p.getRequirements(); j++) {
			if(s.getDecisionVariables()[j].getValue() == 0){
				System.out.print(j);
				if(j+1 != p.getRequirements()){
					System.out.print(", ");
				}
			}				
		}
		System.out.println();
		System.out.println();
		System.out.println("Fitness: "+(-s.getObjective(0)));
		System.out.println("Score: "+p.calculateFitness(s));
		System.out.println("Attend Preferences: "+p.getPreferences().getNumberOfAttendedPref(s)+"/"+p.getPreferences().size());
	}
	
	

}
