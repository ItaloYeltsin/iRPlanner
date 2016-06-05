package br.uece.goes.controller;

import java.io.File;
import java.io.IOException;

import com.sun.corba.se.spi.orbutil.fsm.Action;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jmetal.core.Algorithm;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.interactive.core.Preference;
import jmetal.metaheuristics.iga.IGA;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;
import br.uece.goes.controller.config.Settings;
import br.uece.goes.controller.instance.InstanceCreatorController;
import br.uece.goes.model.PrinterSpreadsheet;
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
	
	@FXML
	ListView<Preference> prefList;

	@FXML
	SplitPane content;

	@FXML
	MenuButton newPref;

	@FXML
	ListView<TableView<String>> solutionView;

	@FXML
	ListView<Solution> saveView;
	
	@FXML
	Button save;
	
	@FXML
	MenuItem openInstance;
	
	@FXML
	MenuItem settingsButton;

	@FXML
	MenuItem instanceCreatorButton;
	
	@FXML
	MenuItem exit;

	@FXML
	Button start;

	@FXML
	Button stop;

	@FXML
	MenuItem saveResultButton;
	
	@FXML
	Text results;
	
	@FXML
	BorderPane pane;

	@FXML
	AnchorPane anchorpane;
	// Controllers

	public static MenuBarController menuBarController;

	public static PreferenceListController prefListController;

	public static SolutionListController solutionListController;

	public static InstanceCreatorController instanceCreatorController;

	public static SaveListController saveListController;
	// Stage
	
	
	Stage stage;

	// Optimization

	public static ReleasePlanningProblem rpp;

	public static Algorithm algorithm;


	String instanceFileName;

	public static Solution finalSolution;

	private Settings settings; 
	
	private Scene configScene = null;
	
	public static MainController mainController;
	
	public MainController() {
		mainController = this;
	}
	
	@FXML
	void initialize() {
		
		instanceCreatorController = InstanceCreatorController.getInstance(this);		
		prefListController = new PreferenceListController(prefList, newPref);
		solutionListController = new SolutionListController(solutionView);
		saveListController = new SaveListController(saveView, save);
		
		
		content.setDisable(true);
		settingsButton.setDisable(true);
		saveResultButton.setDisable(true);
		// Exit Button Action
		exit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});
		
		// Load Settings Screen		
		FXMLLoader loader = new FXMLLoader(Main.class.getResource(
				"Settings.fxml"));		
		try {
			configScene = new Scene((BorderPane) loader.load());
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		settings = loader.getController();
		
		// Open Instance
		openInstance.setOnAction(new OpenInstanceAction ()); 
		
		// Button Settings Action
		settingsButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				settings.update();
				Stage s = new Stage();
				s.setTitle("Settings...");
				s.setAlwaysOnTop(true);
				s.setScene(configScene);
				settings.setScene(s);
				s.initStyle(StageStyle.UTILITY);
				s.showAndWait();
			}
		});
		
		saveResultButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
	              
	              //Set extension filter
	              FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("EXCEL files (.xlsx)", "*.xlsx");
	           
	              fileChooser.getExtensionFilters().add(extFilter);
	              
	              //Show save file dialog
	              File file = fileChooser.showSaveDialog(stage);
	              if(file.getName().contains(extFilter.getExtensions().get(0))) {
	            	  file = new File(file.getAbsolutePath()+extFilter.getExtensions().get(0));
	              }
				
				try {
					new PrinterSpreadsheet().printSolution(rpp, finalSolution, file);
				} catch (JMException e) {
					// TODO Auto-generated catch block
				}
			}
		});
		
		// Optimize Button
		start.setOnAction(new EventHandler<ActionEvent>() {

			@SuppressWarnings("static-access")
			@Override
			public void handle(ActionEvent event) {
				content.setDisable(true);
				try {
					executeAlgorithm();
				} catch (ClassNotFoundException | JMException e) {
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
		
		
	}// initialize

	public void executeAlgorithm() throws JMException, ClassNotFoundException {
		
			finalSolution = algorithm.execute().get(0);
			solutionListController
					.updateSolutionViewer(finalSolution);
			PreferenceListController.XCell.solution = finalSolution;
			results.setText("F: "
					+ Double.toString(-finalSolution.getObjective(0))
					+ " S: "
					+ Double.toString(-finalSolution.getObjective(1)));
			PreferenceListController.XCell.solution = finalSolution;
			prefListController.updateListView();
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

	public BorderPane getPane() {
		return pane;
	}

	public MenuItem getInstanceCreatorButton() {
		return instanceCreatorButton;
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
	
	public AnchorPane getAnchorpane() {
		return anchorpane;
	}

	public SplitPane getContent() {
		return content;
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

	public String getInstanceFileName() {
		return instanceFileName;
	}
	public Solution getFinalSolution() {
		return finalSolution;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public ReleasePlanningProblem getRpp() {
		return rpp;
	}

	public void setRpp(ReleasePlanningProblem rpp) {
		this.rpp = rpp;
	}

	class OpenInstanceAction implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			settings.resetValues();
			settings.setProblem(rpp);
			Stage s = new Stage();
			s.setTitle("Settings...");
			s.initStyle(StageStyle.UNDECORATED);
			s.setAlwaysOnTop(true);
			s.setScene(configScene);
			settings.setScene(s);
			settings.setMainConttroller(mainController);
			
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Choose a instance file");
			chooser.getExtensionFilters().addAll(
					new ExtensionFilter("RPP Instance File", "*.rp"));

			File selectedFile = chooser.showOpenDialog(Main.mainStage);
			if (selectedFile == null)
				return;
			pane.setCenter(anchorpane);
			
			instanceFileName = selectedFile.getName();
			
			try {
				rpp = new ReleasePlanningProblem(selectedFile
						.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Instanciate new GA
			algorithm = new IGA(rpp);
			
			//Preferences list
			PreferenceListController.preferenceAL = rpp.getPreferences()
					.getPreferences();
			solutionListController.createTables(rpp);
			content.setDisable(false);

			// GA configurations
			settings.resetValues();
			settings.setProblem(rpp);
			s.showAndWait();	
			
			// First Execution
			try {
				executeAlgorithm();
			} catch (ClassNotFoundException | JMException e) {
				e.printStackTrace();
			}
			
			settingsButton.setDisable(false);
			saveResultButton.setDisable(false);
		}
		
	}
	
}
