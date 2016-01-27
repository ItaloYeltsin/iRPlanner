package br.uece.goes.controller;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;

public class SolutionEvaluationController {

	@FXML
	ListView solution_1;
	
	@FXML
	ListView solution_2;
	
	@FXML
	Slider slider_1;
	
	@FXML
	Slider slider_2;
	
	@FXML
	Button evaluation;
	
	//Solution Controllers
	public SolutionListController sc_1;
	
	public SolutionListController sc_2;
	
	String instanceFileName;
	
	@FXML
	void initialize() {
		sc_1 = new SolutionListController(solution_1);
		sc_2 = new SolutionListController(solution_2);
		
		evaluation.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				try {
					evaluate();
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void evaluate () throws IOException {
		FileWriter writer = new FileWriter(new File(instanceFileName+".results"), true);
		writer.write("\n----------Subjective Evaluation---------\n");
		writer.write("Non-Interactive: "+slider_1.getValue()+"\n");
		writer.write("Interactive: "+slider_2.getValue()+"\n");
		writer.close();
	}
	
	public void setInstanceFileName(String name) {
		instanceFileName = name;
	}

}
