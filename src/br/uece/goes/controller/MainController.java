package br.uece.goes.controller;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

import br.uece.goes.view.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.interactive.core.Preference;
import jmetal.metaheuristics.iga.IGA;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;
import jmetal.util.Results;

public class MainController {
	// Graphic Interface Elements
	@FXML
	ListView<Preference> prefList;
	
	@FXML
	SplitPane content;
	
	@FXML
	MenuButton newPref;
		
	@FXML
	ListView<TableView<String>> solutionView;
	
	@FXML
	MenuItem openInstance;
	
	@FXML
	Button start;
	// Controllers
	
	MenuBarController menuBarController;
	
	PreferenceListController prefListController;
	
	SolutionListController solutionListController;
	
	// Optimization 
	
	ReleasePlanningProblem rpp;
	
	IGA iga;
	@FXML
	void initialize() {
		prefListController = new PreferenceListController(prefList, newPref);
		solutionListController = new SolutionListController(solutionView);
		content.setDisable(true);
		//Open Instance
		openInstance.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Choose a instance file");
				chooser.getExtensionFilters().addAll(
				         new ExtensionFilter("RPP Instance File", "*.rp"));
				
				File selectedFile = chooser.showOpenDialog(Main.mainStage);
				
				if(selectedFile == null) return;
				
				try {
					rpp = new ReleasePlanningProblem(selectedFile.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				content.setDisable(false);
			
				// GA configurations
				
				IGA iga = new IGA(rpp);
			    
				
			    /* Algorithm parameters*/
			    iga.setInputParameter("populationSize", IGA.POPULATION_SIZE);
			    iga.setInputParameter("elitismRate", IGA.ELITISM_RATE);
			    iga.setInputParameter("nGens", IGA.N_GENS);
				iga.setInputParameter("alpha", 1.0);
				
			}
			
			
		});
		
		// Start Button
		start.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				SolutionSet solutionSet;
				content.setDisable(true);
				try {
					solutionSet = iga.execute();
				} catch (ClassNotFoundException | JMException e) {
					e.printStackTrace();
				}
				content.setDisable(false);
			}
			
			
		});
	
	}

	public MenuBarController getMenuBarController() {
		return menuBarController;
	}

	public void setMenuBarController(MenuBarController menuBarController) {
		this.menuBarController = menuBarController;
	}

	public PreferenceListController getPrefListController() {
		return prefListController;
	}

	public void setPrefListController(PreferenceListController prefListController) {
		this.prefListController = prefListController;
	}

	public SolutionListController getSolutionListController() {
		return solutionListController;
	}

	public void setSolutionListController(
			SolutionListController solutionListController) {
		this.solutionListController = solutionListController;
	}
	
	
}
