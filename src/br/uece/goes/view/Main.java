package br.uece.goes.view;

import java.io.IOException;

import br.uece.goes.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jmetal.util.JMException;

public class Main extends Application {
	public static Stage mainStage;

	@Override
	public void start(Stage mainStage) throws IOException,
			ClassNotFoundException, JMException {
		this.mainStage = mainStage;
		mainStage.setTitle("Interactive Software for Release Planning");
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
				"ReleasePlannerView.fxml"));
		Scene scene = new Scene((BorderPane) loader.load());
		mainStage.setScene(scene);
		mainStage.show();
		MainController mainController = loader.getController();
		mainController.setStage(mainStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
