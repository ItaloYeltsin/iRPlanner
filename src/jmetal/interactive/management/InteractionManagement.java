package jmetal.interactive.management;

import java.io.IOException;
import java.util.Scanner;

import core.InputService;
import jmetal.interactive.HumanInteraction;
import jmetal.interactive.core.PreferencesBase;

public class InteractionManagement {
	private PreferencesBase base;
	private InputService input;
	public InteractionManagement(PreferencesBase base, InputService input) {
		this.base = base;
		this.input = input;
	}

	@SuppressWarnings("resource")
	public void mainMenu() throws IOException {
		boolean exit = false;
		while (!exit) {
			System.out.println("Choose one option:");
			System.out.println("	1 - Add Preferences;");
			System.out.println("	2 - Remove Preferences;");
			System.out.println("	3 - List Preferences;");
			System.out.println("	4 - Exit.");
			int key;
			key = input.getInput();
			switch (key) {
			case 1:
				addMenu();
				break;

			case 2:
				removeMenu();
				break;

			case 3:
				base.listPreferences();
				break;
			case 4:
				exit = true;
			}
		}
	}
	
	private void removeMenu() throws IOException {

		boolean exit = false;
		while (!exit) {
			System.out.println("Choose one option:");
			System.out.println("	1 - Remove a Preference;");
			System.out.println("	2 - Exit.");
			int key = input.getInput();
			if (key == 1) {
				System.out.println("Insert Preference Index: ");
				int index = input.getInput();
				base.remove(index);
				System.out.println("Preference Successfully Removed!");
			} else {
				exit = true;
			}
		}
	}

	private void addMenu() throws IOException {
		boolean exit = false;
		while (!exit) {
			System.out.println("Choose one option:");
			System.out.println("	1 - Coupling Joint;");
			System.out.println("	2 - Coupling DisJoint;");
			System.out.println("	3 - Positioning Precede;");
			System.out.println("	4 - Positioning Follow;");
			System.out.println("	5 - Positioning Before;");
			System.out.println("	6 - Positioning After;");
			System.out.println("	7 - Positioning In.");
			System.out.println("	8 - Exit.");
			int key;
			key = input.getInput();
			;

			int r1, r2, k, distance, weight;
			String type;
			String args;

			switch (key) {

			case 1:
				System.out.println("Insert R1:");
				r1 = input.getInput();
				System.out.println("Insert R2:");
				r2 = input.getInput();
				System.out.println("Insert Preference Level:");
				weight = input.getInput();

				type = "coupling_joint";
				args = r1 + " " + r2;
				base.add(type, args, weight);

				break;

			case 2:
				System.out.println("Insert R1:");
				r1 = input.getInput();
				System.out.println("Insert R2:");
				r2 = input.getInput();
				System.out.println("Insert Preference Level:");
				weight = input.getInput();

				type = "coupling_disjoint";
				args = r1 + " " + r2;
				base.add(type, args, weight);

				break;

			case 3:
				System.out.println("Insert R1:");
				r1 = input.getInput();
				System.out.println("Insert R2:");
				r2 = input.getInput();
				System.out.println("Insert Distance: ");
				distance = input.getInput();
				System.out.println("Insert Preference Level:");
				weight = input.getInput();

				type = "positioning_precede";
				args = r1 + " " + r2 + " " + distance;
				base.add(type, args, weight);

				break;
			case 4:
				System.out.println("Insert R1:");
				r1 = input.getInput();
				System.out.println("Insert R2:");
				r2 = input.getInput();
				System.out.println("Insert Distance: ");
				distance = input.getInput();
				System.out.println("Insert Preference Level:");
				weight = input.getInput();

				type = "positioning_follow";
				args = r1 + " " + r2 + " " + distance;
				base.add(type, args, weight);

				break;
			case 5:
				System.out.println("Insert R1:");
				r1 = input.getInput();
				System.out.println("Insert Release Number:");
				k = input.getInput();
				System.out.println("Insert Distance: ");
				distance = input.getInput();
				System.out.println("Insert Preference Level:");
				weight = input.getInput();

				type = "positioning_before";
				args = r1 + " " + k + " " + distance;
				base.add(type, args, weight);

				break;
			case 6:
				System.out.println("Insert R1:");
				r1 = input.getInput();
				System.out.println("Insert Release Number:");
				k = input.getInput();
				System.out.println("Insert Distance: ");
				distance = input.getInput();
				System.out.println("Insert Preference Level:");
				weight = input.getInput();

				type = "positioning_after";
				args = r1 + " " + k + " " + distance;
				base.add(type, args, weight);

				break;
			case 7:
				System.out.println("Insert R1:");
				r1 = input.getInput();
				System.out.println("Insert Release Number:");
				k = input.getInput();
				System.out.println("Insert Preference Level:");
				weight = input.getInput();

				type = "positioning_in";
				args = r1 + " " + k;
				base.add(type, args, weight);

				break;
			case 8:
				exit = true;
			}
		}
	}

	public boolean exitMenu() {
		while (true) {
			System.out.println("Choose an option: ");
			System.out.println("	1 - I like that solution! I want it!");
			System.out.println("   	2 - No, I prefer to make some changes!");
			int key = input.getInput();
			
			if(key == 1) {
				return true;
			}
			else {
				return false;
			}
		}
	}
}
