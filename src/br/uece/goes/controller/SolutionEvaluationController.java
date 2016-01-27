package br.uece.goes.controller;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;

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
	
	@FXML
	Accordion accordion;
	
	//Solution Controllers
	public SolutionListController sc_1;
	
	public SolutionListController sc_2;
	
	String instanceFileName;
	
	boolean isSet_1 = false;
	boolean isSet_2 = false;
	
	@FXML
	void initialize() {
		sc_1 = new SolutionListController(solution_1);
		sc_2 = new SolutionListController(solution_2);
		
		accordion.setExpandedPane(accordion.getPanes().get(0));
		
		slider_1.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				isSet_1 = true;
			}
		});

		slider_2.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				isSet_2 = true;
			}
		});
		evaluation.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				try {
					if(!isSet_1 || !isSet_2) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Information Dialog");
						alert.setHeaderText("Not all solutions were evaluated");
						alert.setContentText("You must evaluate all solutions!");
						alert.showAndWait();
						return;
					}
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("");
					alert.setHeaderText("The test has been finished");
					alert.setContentText("Thank you for your contribution!");
					alert.showAndWait();
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
