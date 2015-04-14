package jmetal.interactive;

import jmetal.core.Solution;

public class PreferenceFactory{
	Preference makePreference(String type, String args, int weight) {
		if(type == "coupling_joint") {
			return new CouplingJoint(args, weight);
		}
		else if(type == "coupling_disjoint") {
			return new CouplingDisjoint(args, weight);
		}
		else if(type == "positioning_follow") {
			return new PositioningFollow(args, weight);
		}
		else if(type == "positioning_precede") {
			return new PositioningPrecede(args, weight);
		}
		else if(type == "positioning_after") {
			return new PositioningAfter(args, weight);
		}
		else if(type == "positioning_before") {
			return new PositioningBefore(args, weight);
		}
		else if(type == "positioning_in") {
			return new PositioningBefore(args, weight);
		}
		else {
			return null;
		}
	}
}
