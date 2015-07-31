package jmetal.interactive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import jmetal.interactive.core.PreferencesBase;
import core.InputService;

public class InteractionSimulator implements InputService{
	int [] input = {4, 0};
	int count = 0;
	int nOfInteractions = 1;
	
	final int MAX_CHANGES_PREF = 10;
	final int INIT_PREF_RATE = 10;
	final int MAX_INTERAC = 20;
	final int MIN_INTERAC = 10;
	final double EXIT_PROBABILITY = 0.2;
	ArrayList<String>  preferencesList = new ArrayList<String>(100);
	boolean [] flag;
	int baseIndexes[];
	PreferencesBase base;
	Random rand = new Random();
	File log = new File("out/data-set-1.interact.log");
	BufferedWriter buffWriter = new BufferedWriter(new FileWriter(log));
	int nOfAddedPreferences = INIT_PREF_RATE;
	/**
	 * 
	 * @param base
	 * @param preferences
	 * @throws IOException
	 */
	public InteractionSimulator(PreferencesBase base, File preferences) throws IOException {
		this.base = base;
		FileReader reader = new FileReader(preferences);
		BufferedReader buff = new BufferedReader(reader);
		
		while(buff.ready()) {
			preferencesList.add(buff.readLine());
		}
		flag = new boolean[preferencesList.size()];
		baseIndexes = new int[preferencesList.size()];
		HashMap<String, Object> pars;
		
		for (int i = 0; i < INIT_PREF_RATE; i++) {
			while(true) {
				int index = rand.nextInt(flag.length);
				if(!flag[index]) {
					flag[index] = true;
					pars = parse(preferencesList.get(index));
					base.add((String)pars.get("type"),
							(String)pars.get("args"), (Integer)pars.get("weight"));
					baseIndexes[base.size()-1] = index;
					buffWriter.write("add "+preferencesList.get(index));
					buffWriter.newLine();
					break;
				}
			}
		}		
	}
	/**
	 * 
	 */
	@Override
	public int getInput() {
		if(count%2 == 0) {
			try {
				Interact();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				switchKey();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		nOfInteractions += count%2;
		return input[count++%2];
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	void Interact() throws IOException {
		if(nOfInteractions == 1 ) return;
		buffWriter.write("=============Nova Interação===========");
		buffWriter.newLine();
		int iterationsCounter = 0;
		HashMap <String, Object> prefArgs;
		int max_interactions = rand.nextInt(MAX_CHANGES_PREF)+1;
		
		while (iterationsCounter < max_interactions) {
			double prob = rand.nextDouble(); 
			if(prob <= 0.33) { // ADD
				while(base.size() < preferencesList.size()) {
					int index = rand.nextInt(preferencesList.size());
					
					if(!flag[index]) {
						flag[index] = true;
						prefArgs  = parse(preferencesList.get(index));
						base.add((String)prefArgs.get("type"),
								(String)prefArgs.get("args"), (Integer)prefArgs.get("weight"));
						baseIndexes[base.size()-1] = index;
						iterationsCounter++;
						buffWriter.write("add "+preferencesList.get(index));
						buffWriter.newLine();
						break;
					}
					
				}
			}else if(prob > 0.33 && prob <= 0.66) { //DELETE
				if(base.size() > 0) {
					int pref = rand.nextInt(base.size());
					base.remove(pref);
					flag[baseIndexes[pref]] = false;
					buffWriter.write("remover "+pref);
					buffWriter.newLine();
					iterationsCounter++;
				}
			}else { // CHANGE
				if(base.size() > 0) {
					int index = rand.nextInt(base.size());
					int weight = rand.nextInt(10)+1;
					base.edit(index, weight);
					buffWriter.write("alterar "+index+" "+weight);
					buffWriter.newLine();
					iterationsCounter++;
				}
			}
		}
		
		
	}
	/**
	 * 
	 * @param pref
	 * @return
	 */
	HashMap<String, Object> parse (String pref) {
		HashMap<String, Object> pars = new HashMap<String, Object>();
		String[] args = pref.split(" ");
		pars.put("type", args[0]);
		pars.put("weight", Integer.parseInt(args[args.length-1]));
		String reqs = "";
		for (int i = 1; i < args.length - 1; i++) {
			reqs += args[i]+" ";
		}
		pars.put("args", reqs);
			
		return pars;
	}
	/**
	 * @throws IOException 
	 * 
	 */
	void switchKey() throws IOException{
		if((new Random().nextDouble() < EXIT_PROBABILITY 
				|| nOfInteractions == MAX_INTERAC) 
				&& nOfInteractions > MIN_INTERAC ) {
			input[1] = 1;
			buffWriter.close();
		}
		
	}
	
	
}
