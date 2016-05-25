package br.uece.goes.controller.config.metaheuristics;

import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jmetal.core.Algorithm;

public class MountGAConfigScreen extends MountAlgConfigScreenTemplate{

	protected MountGAConfigScreen() {
	}
	
	@Override
	public void applyFunction() {
		ObservableList list = vbox.getChildren();
		TextField input;
		
		input = (TextField) ((GridPaneLayout) list.get(0)).getNode();
		algorithm.setInputParameter("populationSize", Double.parseDouble(input.getText()));
		
		input = (TextField) ((GridPaneLayout) list.get(1)).getNode();
		algorithm.setInputParameter("mutationRate", Double.parseDouble(input.getText()));
		
		input = (TextField) ((GridPaneLayout) list.get(2)).getNode();
		algorithm.setInputParameter("crossoverRate", Double.parseDouble(input.getText()));
		
		input = (TextField) ((GridPaneLayout) list.get(3)).getNode();
		algorithm.setInputParameter("nGens", Double.parseDouble(input.getText()));
		
		input = (TextField) ((GridPaneLayout) list.get(4)).getNode();
		algorithm.setInputParameter("elitismRate", Double.parseDouble(input.getText()));
	}

	@Override
	protected void setElements(VBox vbox) {
		ObservableList list = vbox.getChildren();
		list.add(
				new GridPaneLayout("Population size:", new TextField(algorithm.getInputParameter("populationSize")+"")));
		list.add(
				new GridPaneLayout("Mutation rate:", new TextField(algorithm.getInputParameter("mutationRate")+"")));
		list.add(
				new GridPaneLayout("Crossover rate:", new TextField(algorithm.getInputParameter("crossoverRate")+"")));
		list.add(
				new GridPaneLayout("Nº of generations:", new TextField(algorithm.getInputParameter("nGens")+"")));
		list.add(
				new GridPaneLayout("Elitism rate:", new TextField(algorithm.getInputParameter("elitismRate")+"")));
	}
}
