package jmetal.print.results;

import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.problems.ReleasePlanningProblem;

public class PrintUserPreference extends PrintStats {

	@Override
	public void print(SolutionSet set, Problem problem) {
		System.out.println("----------------------------");
		
		ReleasePlanningProblem p = (ReleasePlanningProblem) problem;
		
		//Print requirements that are together
		System.out.println("Together");
		for(int[] a : p.getConstraints()){
			if(a[2] == 1){
				System.out.println("\t"+a[0]+" and "+a[1]);
			}
		}
		
		//Print requirements that are separate
		System.out.println("Separate");
		for(int[] a : p.getConstraints()){
			if(a[2] == 0){
				System.out.println("\t"+a[0]+" and "+a[1]);
			}
		}
	}
	
	

}
