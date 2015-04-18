package jmetal.interactive.preferences.simulator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Simulator {
	
	public String preferencesFilePath;
	ArrayList<String> PreferencesAssertion = new ArrayList<String>(); 
	
	@SuppressWarnings("resource")
	public Simulator (String preferencesFilePath) throws FileNotFoundException {
		this.preferencesFilePath = preferencesFilePath;
		FileReader reader = new FileReader(this.preferencesFilePath);		
		Scanner scanner = new Scanner(reader).useDelimiter("\n");
		
		while (scanner.hasNext()) {
			String assertion = scanner.next();
			PreferencesAssertion.add(assertion);
		}
		scanner.close();
		
		Collections.shuffle(PreferencesAssertion);		
	}
	
	// return one random preference assertion from the list
	public HashMap getOneUserPrefence(int index){
		
		String [] params = PreferencesAssertion.get(index).split(" ");
		
		String type = params[0];
		String args = "";
		
		for (int i = 1; i < params.length - 2; i++) {
			args += params[i] + " ";
		}
		args += params[params.length-2];
		String level = params[params.length-1]; 
		level = level.trim();
		HashMap preference = new HashMap<String, String>(); 
		
		preference.put("type", type);
		preference.put("args", args);
		preference.put("level", level);
		
		return preference;
	}
	
	// return a Hashmap ArrayList with a subset of preference assertions
	public ArrayList<HashMap> getUserPreferences(int number){
		
		ArrayList<HashMap> set = new ArrayList<HashMap>();
		
		for (int i = 0; i < number; i++) {
			HashMap<String,String> hashMap = getOneUserPrefence(i);  
			set.add(hashMap);
		}
		
		return set;
	}
	
}