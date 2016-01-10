package jmetal.interactive.core;

import jmetal.core.Solution;
import jmetal.interactive.preferences.CouplingDisjoint;
import jmetal.interactive.preferences.CouplingJoint;
import jmetal.interactive.preferences.PositioningAfter;
import jmetal.interactive.preferences.PositioningBefore;
import jmetal.interactive.preferences.PositioningFollow;
import jmetal.interactive.preferences.PositioningIn;
import jmetal.interactive.preferences.PositioningPrecede;

public class PreferenceFactory{
	public static Preference makePreference(String type, String args, int weight) {
		if(type.equals("coupling_joint")) {
			return new CouplingJoint(args, weight);
		}
		else if(type.equals("coupling_disjoint")) {
			return new CouplingDisjoint(args, weight);
		}
		else if(type.equals("positioning_follow")) {
			return new PositioningFollow(args, weight);
		}
		else if(type.equals("positioning_precede")) {
			return new PositioningPrecede(args, weight);
		}
		else if(type.equals("positioning_after")) {
			return new PositioningAfter(args, weight);
		}
		else if(type.equals("positioning_before")) {
			return new PositioningBefore(args, weight);
		}
		else if(type.equals("positioning_in")) {
			return new PositioningIn(args, weight);
		}
		else {
			throw new IllegalArgumentException("There's no such type of preference: -"+type+"-");
		}
	}
}
