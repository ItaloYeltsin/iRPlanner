package br.uece.goes.controller.config.metaheuristics;

import javafx.scene.layout.VBox;

public class MountAlgConfigScreenFactory extends MountAlgConfigScreenTemplate{
	
	static MountGAConfigScreen ga;
	//Algorithms
	public final static String GA = "Genetic Algorithm"; 
	
	public static MountAlgConfigScreenTemplate getInstance(String windowType) {
		if(GA.equals(windowType)) {
			if(ga != null) return ga;
			ga = new MountGAConfigScreen();
			return ga;
		}
		
		return null;
	}
	
	@Override
	public void applyFunction() {
		
	}

	@Override
	protected void setElements(VBox vbox) {
		
	}
		
}
