package br.uece.goes.view;

import java.io.File;
import java.io.IOException;

import br.uece.goes.controller.MainController;
import br.uece.goes.controller.PreferenceListController;
import br.uece.goes.controller.SolutionListController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jmetal.core.Solution;
import jmetal.interactive.core.PreferenceFactory;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;

public class Main extends Application {
	public static Stage mainStage;

	@Override
	public void start(Stage mainStage) throws IOException,
			ClassNotFoundException, JMException {
		this.mainStage = mainStage;
		mainStage.setTitle("Interactive Release Planning Software");
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
				"ReleasePlannerView.fxml"));
		mainStage.setScene(new Scene((BorderPane) loader.load()));
		MainController mainController = loader.getController();
		PreferenceListController.preference.add(PreferenceFactory
				.makePreference("coupling_joint", "10 1", 10));
		PreferenceListController.preference.add(PreferenceFactory
				.makePreference("coupling_disjoint", "4 1", 4));
		ReleasePlanningProblem problem = new ReleasePlanningProblem(
				"in/data-set-1.rp");
		Solution s = new Solution(problem);
		SolutionListController slc = mainController.getSolutionListController();
		mainStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
