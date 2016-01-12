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
import br.uece.goes.view.elements.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.interactive.core.Preference;
import jmetal.interactive.core.PreferenceFactory;
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

		// Open Instance
		openInstance.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Choose a instance file");
				chooser.getExtensionFilters().addAll(
						new ExtensionFilter("RPP Instance File", "*.rp"));

				File selectedFile = chooser.showOpenDialog(Main.mainStage);

				if (selectedFile == null)
					return;

				try {
					rpp = new ReleasePlanningProblem(selectedFile
							.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}

				PreferenceListController.preferenceAL = rpp.getPreferences()
						.getPreferences();
				solutionListController.createTables(rpp);
				content.setDisable(false);

				// GA configurations

				iga = new IGA(rpp);

				/* Algorithm parameters */
				iga.setInputParameter("populationSize", IGA.POPULATION_SIZE);
				iga.setInputParameter("elitismRate", IGA.ELITISM_RATE);
				iga.setInputParameter("nGens", IGA.N_GENS);
				iga.setInputParameter("alpha", 1.0);

				// Operators

				try {
					iga.addOperator("crossover", getCrossoverOperator());
				} catch (JMException e) {
					e.printStackTrace();
				}
				try {
					iga.addOperator("mutation", getMutationOperator());
				} catch (JMException e) {
					e.printStackTrace();
				}
				try {
					iga.addOperator("selection", getSelectionOperator());
				} catch (JMException e) {
					e.printStackTrace();
				}

				try {
					solutionListController.updateSolutionViewer(iga.execute()
							.get(0));
				} catch (ClassNotFoundException | JMException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Operator getMutationOperator() throws JMException {
				HashMap parameters = new HashMap();
				parameters.put("probability", 0.01);

				return MutationFactory.getMutationOperator("BitFlipMutation",
						parameters);
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Operator getCrossoverOperator() throws JMException {
				HashMap parameters = new HashMap();
				parameters.put("probability", 0.9);

				return CrossoverFactory.getCrossoverOperator(
						"SinglePointCrossover", parameters);
			}

			public Operator getSelectionOperator() throws JMException {
				return SelectionFactory.getSelectionOperator(
						"BinaryTournament", null);
			}

		}); // End of Open Instance

		// Start Button
		start.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				SolutionSet solutionSet = null;
				content.setDisable(true);
				try {
					solutionSet = iga.execute();
				} catch (ClassNotFoundException | JMException e) {
					e.printStackTrace();
				}

				try {
					solutionListController.updateSolutionViewer(solutionSet
							.get(0));
				} catch (JMException e) {
					e.printStackTrace();
				}
				content.setDisable(false);
			}

		});

		try {
			setTypesOfPreferences();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setTypesOfPreferences() throws IOException {
		ObservableList<MenuItem> array = newPref.getItems();
		array.clear();
		Stage stage = new Stage();
		stage.setResizable(false);
		// Coupling Joint
		MenuItem button = new MenuItem("Coupling Joint");
		array.add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Window w = new CouplingJointWindow(rpp, stage);
				stage.setScene(new Scene(w.getPane()));
				stage.show();
			}
		});

		// Coupling Disjoint
		button = new MenuItem("Coupling Distjoint");
		array.add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Window w = new CouplingDisjointWindow(rpp, stage);
				stage.setScene(new Scene(w.getPane()));
				stage.show();
			}
		});

		// Positioning Precede
		button = new MenuItem("Positioning Precede");
		array.add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Window w = new PositioningPrecede(rpp, stage);
				stage.setScene(new Scene(w.getPane()));
				stage.show();
			}
		});

		// Positioning Follow
		button = new MenuItem("Positioning Follow");
		array.add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Window w = new PositioningFollow(rpp, stage);
				stage.setScene(new Scene(w.getPane()));
				stage.show();
			}
		});

		// Positioning Follow
		button = new MenuItem("Positioning Before");
		array.add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Window w = new PositioningBefore(rpp, stage);
				stage.setScene(new Scene(w.getPane()));
				stage.show();
			}
		});

		// Positioning Follow
		button = new MenuItem("Positioning In");
		array.add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Window w = new PositioningIn(rpp, stage);
				stage.setScene(new Scene(w.getPane()));
				stage.show();
			}
		});

		// Positioning Follow
		button = new MenuItem("Positioning After");
		array.add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Window w = new PositioningAfter(rpp, stage);
				stage.setScene(new Scene(w.getPane()));
				stage.show();
			}
		});

		// Positioning Follow
		button = new MenuItem("Positioning No");
		array.add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Window w = new PositioningNo(rpp, stage);
				stage.setScene(new Scene(w.getPane()));
				stage.show();
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

	public void setPrefListController(
			PreferenceListController prefListController) {
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
