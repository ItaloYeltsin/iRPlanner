package br.uece.goes.controller.config.metaheuristics;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jmetal.core.Algorithm;

public abstract class MountAlgConfigScreenTemplate {
	
	static protected VBox vbox;
	static protected Algorithm algorithm;
	/**
	 * Mount a configuration Window
	 * @param vbox
	 * @param algorithm
	 */
	public void execute(VBox vbox, Algorithm algorithm){
		this.vbox = vbox;
		this.algorithm = algorithm;
		vbox.getChildren().clear();		
		setElements(vbox);
	}
	
	/**
	 * this method has as function the insertion of the new parameters values in the optimization algorithm
	 */
	public abstract void applyFunction();
	
	/**
	 * This method is to set all parameters fields related to an algorithm
	 * @param vbox
	 */
	protected abstract void setElements(VBox vbox);
}
