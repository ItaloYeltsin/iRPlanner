package br.uece.goes.controller;


import java.util.ArrayList;

import br.uece.goes.view.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import jmetal.core.Solution;
import jmetal.interactive.core.Preference;
import jmetal.util.JMException;

public class PreferenceListController{
	// Preference items
	public static ObservableList<Preference> preference = FXCollections.observableArrayList();
	public static ArrayList<Preference> preferenceAL;
	// Preference List View
	public ListView<Preference> prefList;

	private MenuButton newPref;
	
	public PreferenceListController(ListView<Preference> prefList, MenuButton newPref) {
		this.prefList = prefList;
		this.newPref = newPref;
		initialize();
	}
	
	
	private void initialize() {
		prefList.setItems(preference);
		prefList.setCellFactory(new Callback<ListView<Preference>, ListCell<Preference>>() {
			
			@Override
			public ListCell<Preference> call(ListView<Preference> param) {
				// TODO Auto-generated method stub
				return new XCell();			
			}
		});
		
		newPref.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		Image imageAddPreff = new Image(Main.class.getResourceAsStream("images/add.png"));
		newPref.setGraphic(new ImageView(imageAddPreff));
	
	}
	
	
	static class XCell extends ListCell<Preference> {
		public HBox hbox = new HBox();
		public VBox vbox = new VBox();
				
		Label label = new Label("(empty)");
		Pane pane = new Pane();
		Button satisfiedButton = new Button();
		Button editButton = new Button();
		Button deleteButton = new Button();
		
		Preference lastItem;

		
		public XCell(){ 
			super();
			
			label.setStyle("-fx-label-padding:4 0 0 0");
			
			Image imageDelete 	= new Image(Main.class.getResourceAsStream("images/delete.png"));
			Image imageEdit 	= new Image(Main.class.getResourceAsStream("images/edit.png"));
			Image imageSatisfied= new Image(Main.class.getResourceAsStream("images/satisfied.png"));
			
			deleteButton.setGraphic(new ImageView(imageDelete));
			deleteButton.setBackground(null);
			
			editButton.setGraphic(new ImageView(imageEdit));
			editButton.setBackground(null);
			
			satisfiedButton.setGraphic(new ImageView(imageSatisfied));
			satisfiedButton.setBackground(null);
			
			editButton.setOnMouseEntered(new MouseOn(editButton, true));
			editButton.setOnMouseExited(new MouseOn(editButton, false));
			
			deleteButton.setOnMouseEntered(new MouseOn(deleteButton, true));
			deleteButton.setOnMouseExited(new MouseOn(deleteButton, false));
			
			hbox.getChildren().addAll(satisfiedButton, label, pane, editButton, deleteButton);
			HBox.setHgrow(pane, Priority.ALWAYS);
			vbox.getChildren().add(hbox);
		}

		public class MouseOn implements EventHandler<Event> {
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
				if(isMouseOn) {
					b.setEffect(shadow);
				}else{
					b.setEffect(null);
				}
				
			}
			
		};
		
		@Override
		protected void updateItem(Preference item, boolean empty) {
			super.updateItem(item, empty);
			setText(null);  // No text in label of super class
			if (empty) {
				lastItem = null;
				setGraphic(null);
			} else {
				lastItem = item;
				label.setText(lastItem.getLogicalExpression());
				setGraphic(vbox);
			}
			
			editButton.setOnAction(new EventHandler<ActionEvent>() {
				
				boolean isSliderActived = false;
				Slider slider;
				@Override
				public void handle(ActionEvent event) {
					if(isSliderActived) {
						vbox.getChildren().remove(slider);
						isSliderActived = false;
						return;
					}
					
					isSliderActived = true;
					slider = new Slider(1, 10, item.getWeight());
					slider.setShowTickLabels(true);
					slider.setShowTickMarks(true);
					slider.setMajorTickUnit(3);
					slider.setMinorTickCount(2);
					slider.setBlockIncrement(1);
					slider.setPadding(new Insets(5, 0, 5, 0));
					vbox.getChildren().add(slider);
					slider.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
						
						@Override
						public void handle(MouseEvent event) {
							vbox.getChildren().remove(slider);
							
							item.setWeight((int)Math.round(slider.getValue()));
							isSliderActived = false;
						}
					});
				}
			});
			
			deleteButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					preference.remove(item);
					preferenceAL.remove(item);
				}
			});
			
		}
	}
	
	public static void addPreference(Preference pref) {
		preference.add(pref);
		preferenceAL.add(pref);
	}
}
