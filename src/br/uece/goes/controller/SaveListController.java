package br.uece.goes.controller;

import java.io.IOException;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import br.uece.goes.controller.PreferenceListController.XCell;
import br.uece.goes.controller.PreferenceListController.XCell.MouseOn;
import br.uece.goes.view.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import jmetal.core.Solution;

public class SaveListController {

	private ListView<Solution> saveView;
	private Button save;
	static int count = 1;
	static ObservableList<Solution> itens = FXCollections.observableArrayList();
	
	public SaveListController(ListView<Solution> saveView, Button save) {
		this.saveView = saveView;
		this.save = save;
		initialize();

	}

	void initialize() {

		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (count <= 5) {
					if (!itens.contains(MainController.finalSolution)) {
						itens.add(MainController.finalSolution);
						count++;
						saveView.setItems(itens);
					}
				}

			}
		});
		saveView.setCellFactory(new Callback<ListView<Solution>, ListCell<Solution>>() {

			@Override
			public ListCell<Solution> call(ListView<Solution> param) {
				return new XCell();
			}
		});

	}

	static void startPane(Solution item) {

		try {
			FrozenSolutionController.finalSolution = item;
			FrozenSolutionController.rpp = MainController.rpp;
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(
					Main.class.getResource("FrozenSolutionView.fxml"));
			Scene scene;
			scene = new Scene((AnchorPane) loader.load());
			stage.setScene(scene);
			FrozenSolutionController.stage = stage;
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static class XCell extends ListCell<Solution> {
		public HBox hbox = new HBox();
		public VBox vbox = new VBox();
		final String [] subs = {"\u2081", "\u2082", "\u2083", "\u2084", "\u2085"};

		Label label = new Label("(empty)");
		Pane pane = new Pane();

		Button openSolutionButton = new Button();
		Button deleteButton = new Button();

		Image imageDelete = new Image(
				Main.class.getResourceAsStream("images/delete.png"));

		public XCell() {
			super();
			
			label.setStyle("-fx-label-padding:4 0 0 0");

			deleteButton.setGraphic(new ImageView(imageDelete));
			deleteButton.setBackground(null);

			openSolutionButton.setGraphic(new Glyph("FontAwesome",
					FontAwesome.Glyph.SEARCH));
			openSolutionButton.setBackground(null);

			openSolutionButton.setOnMouseEntered(new MouseOn(
					openSolutionButton, true));
			openSolutionButton.setOnMouseExited(new MouseOn(openSolutionButton,
					false));

			deleteButton.setOnMouseEntered(new MouseOn(deleteButton, true));
			deleteButton.setOnMouseExited(new MouseOn(deleteButton, false));

			hbox.getChildren().addAll(label, pane, openSolutionButton,
					deleteButton);
			HBox.setHgrow(pane, Priority.ALWAYS);
			vbox.getChildren().add(hbox);

		}

		class MouseOn implements EventHandler<Event> {
			Button b;
			Boolean isMouseOn;
			DropShadow shadow = new DropShadow();

			MouseOn(Button b, Boolean isMouseOn) {
				this.b = b;
				this.isMouseOn = isMouseOn;
				shadow.setColor(Color.color(0.01, 0.61, 0.82));
			}

			@Override
			public void handle(Event arg0) {
				if (isMouseOn) {
					b.setEffect(shadow);
				} else {
					b.setEffect(null);
				}
			}
		}

		@Override
		protected void updateItem(Solution item, boolean empty) {

			super.updateItem(item, empty);
			setText(null); // No text in label of super class

			if (empty) {
				// lastItem = null;
				setGraphic(null);
			} else {
				label.setText("S"+subs[itens.indexOf(item)]);
				setGraphic(vbox);
			}

			openSolutionButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					startPane(item);
				}
			});
			
			deleteButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					itens.remove(item);
					count--;
					
				}
			});

		}
	}

}
