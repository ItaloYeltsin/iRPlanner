package jmetal.interactive.core;

import java.util.ArrayList;

import com.bethecoder.ascii_table.ASCIITable;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.JMException;

public class PreferencesBase {
	/**
	 * List of Preferences
	 */
	private ArrayList<Preference> preferences;
	/**
	 * Constructor
	 */
	public PreferencesBase() {
		preferences = new ArrayList<Preference>(100);
	}
	
	/**
	 * 
	 * @param type
	 * @param args
	 */
	public void add(String type, String args, int weight) {
		preferences.add(new PreferenceFactory().makePreference(type, args, weight));	
	}
	/**
	 * Remove preference by its index
	 * @param index
	 */
	public void remove(int index) {
		preferences.remove(index-1);
	}
	
	/**
	 * Evaluate solution in relation to non-attended preferences
	 * @param solution
	 * @return
	 * @throws JMException 
	 */
	public double evaluate(Solution solution) throws JMException {
		
		if(preferences.size() == 0) return 0;
		
		double nonAttendedPreferenceRate = 0;
		double weightsSum = 0;
		
		for (Preference preference : preferences) {
			nonAttendedPreferenceRate += (1 - preference.evaluate(solution))*preference.getWeight();
			weightsSum += preference.getWeight();
		}
		//Weight Average
		nonAttendedPreferenceRate = nonAttendedPreferenceRate/weightsSum;
		
		return nonAttendedPreferenceRate;
	}
	/**
	 * 
	 * @return number of preferences
	 */
	public int size() {
		return preferences.size();
	}
	/**
	 * print preferences
	 */
	public void listPreferences() {
		int count = 0;
		if(size() == 0) return;
		String [] header = {"Index", "Logical Expression", "Preference Weight"};
		
		String[][] content = new String[preferences.size()][3];
		for (Preference preference : preferences) {
			content[count][0] = (count+1)+"";
			content[count][1] = preference.toString();
			content[count++][2] = preference.getWeight()+"";
		}
		
		ASCIITable.getInstance().printTable(header, content);
	}
	
	public void listPreferences(Solution solution) throws JMException {
		int count = 0;
		if(size() == 0) return;
		String [] header = {"Index", "Logical Expression", "Preference Weight", "Attended(Yes/No)"};
		String [] attended = {"No", "Yes"};
		String[][] content = new String[preferences.size()][4];
		for (Preference preference : preferences) {
			content[count][0] = (count+1)+"";
			content[count][1] = preference.toString();
			content[count][2] = preference.getWeight()+"";
			content[count++][3] = attended[preference.evaluate(solution)];
		}
		
		ASCIITable.getInstance().printTable(header, content);
	}

	public int getNumberOfAttendedPref(Solution s) throws JMException {
		// TODO Auto-generated method stub
		int counter = 0;
		for (Preference preference : preferences) {
			counter += preference.evaluate(s);
		}
		return counter;
	}
}
