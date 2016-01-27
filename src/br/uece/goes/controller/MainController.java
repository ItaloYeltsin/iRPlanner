package br.uece.goes.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
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
import br.uece.goes.view.Main;
import br.uece.goes.view.elements.CouplingDisjointWindow;
import br.uece.goes.view.elements.CouplingJointWindow;
import br.uece.goes.view.elements.PositioningAfter;
import br.uece.goes.view.elements.PositioningBefore;
import br.uece.goes.view.elements.PositioningFollow;
import br.uece.goes.view.elements.PositioningIn;
import br.uece.goes.view.elements.PositioningNo;
import br.uece.goes.view.elements.PositioningPrecede;
import br.uece.goes.view.elements.Window;

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
	MenuItem exit;

	@FXML
	Button start;

	@FXML
	Button stop;

	@FXML
	Text results;

	// Controllers

	MenuBarController menuBarController;

	PreferenceListController prefListController;

	SolutionListController solutionListController;

	// Stage
	Stage stage;

	// Optimization

	ReleasePlanningProblem rpp;

	IGA iga;

	FileWriter writer;

	Solution non_interactive;

	final String METRICS_HEAD = "#Fitness\t#Score\t#SP(PS)\t#SL\t#QTD. PREF";

	String instanceFileName;

	private Solution finalSolution;

	@FXML
	void initialize() {
		prefListController = new PreferenceListController(prefList, newPref);
		solutionListController = new SolutionListController(solutionView);
		content.setDisable(true);

		// Exit Button

		exit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		// Open Instance
		openInstance.setOnAction(new EventHandler<ActionEvent>() {

			@SuppressWarnings("static-access")
			@Override
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Choose a instance file");
				chooser.getExtensionFilters().addAll(
						new ExtensionFilter("RPP Instance File", "*.rp"));

				File selectedFile = chooser.showOpenDialog(Main.mainStage);
				if (selectedFile == null)
					return;

				instanceFileName = selectedFile.getName();

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
					non_interactive = iga.execute().get(0);
					finalSolution = non_interactive;
					saveResults(finalSolution);
					solutionListController
							.updateSolutionViewer(non_interactive);
					PreferenceListController.XCell.solution = non_interactive;
					results.setText("F: "
							+ Double.toString(-non_interactive.getObjective(0))
							+ " S: "
							+ Double.toString(-non_interactive.getObjective(1)));
				} catch (ClassNotFoundException | JMException | IOException e1) {
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

			@SuppressWarnings("static-access")
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
					PreferenceListController.XCell.solution = solutionSet
							.get(0);
					finalSolution = solutionSet.get(0);
					ObservableList<Preference> list = prefList.getItems();
					prefList.setItems(null);
					prefList.setItems(list);
					results.setText("F: "
							+ Double.toString(-solutionSet.get(0).getObjective(
									0))
							+ " S: "
							+ Double.toString(-solutionSet.get(0).getObjective(
									1)));
					saveResults(solutionSet.get(0));
				} catch (JMException | IOException e) {
					e.printStackTrace();
				}
				content.setDisable(false);
			}

		});

		try {
			setTypesOfPreferences();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Stop Action
		stop.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FXMLLoader loader = new FXMLLoader(Main.class.getResource(
						"SolutionsEvaluation.fxml"));
				Scene scene = null;
				try {
					scene = new Scene((BorderPane) loader.load());
				} catch (IOException e) {
					e.printStackTrace();
				}
				SolutionEvaluationController controller = loader.getController();
				controller.setInstanceFileName(instanceFileName);
				controller.sc_1.createTables(rpp);
				try { 
					controller.sc_1.updateSolutionViewer(non_interactive);
				} catch (JMException e) {
					e.printStackTrace();
				}
				controller.sc_2.createTables(rpp);
				try {
					controller.sc_2.updateSolutionViewer(finalSolution);
				} catch (JMException e) {
					e.printStackTrace();
				}

				stage.setScene(scene);
				stage.show();
			}
		});

	}// initialize

	void saveResults(Solution solution) throws IOException, JMException {
		writer = new FileWriter(new File(instanceFileName + ".results"));
		writer.write("-------------- Non-Interactive --------------\n");
		writer.write(METRICS_HEAD + "\n");
		int nOfPrefs = rpp.getPreferences().size();
		double SL;
		double SP;
		if (nOfPrefs == 0) {
			SL = 0;
			SP = 0;
		} else {
			SP = rpp.getPreferences().getNumberOfAttendedPref(non_interactive)
					/ (double) nOfPrefs;
			SL = rpp.getPreferences().getWeightSumOfSatisfiedPref(
					non_interactive)
					/ (double) rpp.getPreferences().getWeightSumOfAllPref();
		}
		writer.write(-non_interactive.getObjective(0) + "\t"
				+ -non_interactive.getObjective(1) + "\t" + SP + "\t" + SL
				+ "\t" + nOfPrefs + "\n");

		writer.write("---------------- Interactive ----------------\n");
		writer.write(METRICS_HEAD + "\n");
		if (nOfPrefs == 0) {
			SL = 0;
			SP = 0;
		} else {
			SP = rpp.getPreferences().getNumberOfAttendedPref(solution)
					/ (double) nOfPrefs;
			SL = rpp.getPreferences().getWeightSumOfSatisfiedPref(solution)
					/ (double) rpp.getPreferences().getWeightSumOfAllPref();
		}
		writer.write(-solution.getObjective(0) + "\t"
				+ -solution.getObjective(1) + "\t" + SP + "\t" + SL + "\t"
				+ nOfPrefs);
		writer.close();
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

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Solution getNon_interactive() {
		return non_interactive;
	}

	public String getInstanceFileName() {
		return instanceFileName;
	}
	public Solution getFinalSolution() {
		return finalSolution;
	}

}
