package br.uece.goes.view.elements;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import br.uece.goes.controller.NewPreferenceController;
import br.uece.goes.controller.PreferenceListController;
import br.uece.goes.view.Main;
import jmetal.interactive.core.Preference;
import jmetal.problems.ReleasePlanningProblem;

public abstract class Window {
	
	protected NewPreferenceController controller;
	protected ReleasePlanningProblem rpp;
	protected BorderPane pane;

	final static protected Pane GROW_PANE_1 = new Pane();
	final static protected Pane GROW_PANE_2 = new Pane();
	Stage stage;
	Window(ReleasePlanningProblem rpp, Stage stage) {
		this.rpp = rpp;
		this.stage = stage;
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("NewPreferenceView.fxml"));
		try {
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		controller = loader.getController();
	}
	
	public BorderPane getPane() {
		return pane;
	}
	
	protected void addPreference(Preference e) {
		PreferenceListController.addPreference(e);
		stage.close();
	}
}
