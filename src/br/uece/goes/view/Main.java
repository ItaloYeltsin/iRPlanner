package br.uece.goes.view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
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
		mainStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
