package jmetal.interactive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.hamcrest.BaseDescription;

import jmetal.interactive.core.PreferencesBase;
import core.InputService;

public class SimulatorLogDriven implements InputService {
	PreferencesBase base;
	BufferedReader buff;
	int[] input = { 4, 0 };
	private int count;
	private int nOfInteractions;

	public SimulatorLogDriven(PreferencesBase base, File log) throws FileNotFoundException {
		buff = new BufferedReader(new FileReader(log));
		this.base = base;
	}

	@Override
	public int getInput() {
		if (count % 2 == 0) {
			try {
				Interact();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		nOfInteractions += count % 2;
		return input[count++ % 2];
	}

	private void Interact() throws IOException {
		HashMap parse;
		
		String command = buff.readLine();
		System.out.println(command);
		while(command != null && command.getBytes()[0] != '=') {
			
			if(command.contains("add ")) {
				command = command.replace("add ", "");
				parse = parse(command);
				base.add((String)parse.get("type"),
						(String)parse.get("args"), (Integer)parse.get("weight"));
				
			}else if(command.contains("remover ")) {
				command = command.replace("remover ", "");
				base.remove(Integer.parseInt(command));
			}else {
				command = command.replace("alterar ", "");
				String [] alter = command.split(" ");
				base.edit(Integer.parseInt(alter[0]), Integer.parseInt(alter[1]));
			}
			command = buff.readLine();
			System.out.println(command);
		}
		if(command == null) {
			input[1] = 1;
		}
	}
	
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

}
