package jmetal.interactive;

import java.util.Scanner;

import core.InputService;

public class HumanInteraction implements InputService{

	@Override
	public int getInput() {
		return new Scanner(System.in).nextInt();
	}
	
}
