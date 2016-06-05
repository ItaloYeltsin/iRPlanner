package br.uece.goes.controller;

import java.io.File;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import br.uece.goes.model.PrinterSpreadsheet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jmetal.core.Solution;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;

public class FrozenSolutionController {

	public static Solution finalSolution;
	public static ReleasePlanningProblem rpp;
	public static Stage stage;
	
	@FXML
	Button save;
	
	@FXML
	ListView<TableView<String>> solutionView;
	SolutionListController solutionListController;

	@FXML
	void initialize() {

		try {

			solutionListController = new SolutionListController(solutionView);
			solutionListController.createTables(rpp);
			solutionListController.updateSolutionViewer(finalSolution);
			
			save.setGraphic(new Glyph("FontAwesome",FontAwesome.Glyph.SAVE));
			
		} catch (JMException e) {
			e.printStackTrace();
		}
		
		
		save.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				try {
					
		              FileChooser fileChooser = new FileChooser();
		              
		              //Set extension filter
		              FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("EXCEL files (.xlsx)", "*.xlsx");
		              fileChooser.getExtensionFilters().add(extFilter);
		              
		              //Show save file dialog
		              File file = fileChooser.showSaveDialog(stage);
					
					
					new PrinterSpreadsheet().printSolution(rpp, finalSolution, file);
					stage.close();
				} catch (JMException e) {
					e.printStackTrace();
				}
				
			}
		});

	}
	
	

}
