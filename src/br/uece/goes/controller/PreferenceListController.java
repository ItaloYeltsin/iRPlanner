package br.uece.goes.controller;


import java.util.ArrayList;

import br.uece.goes.view.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jmetal.interactive.core.Preference;

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
		
		// ADD Button
		
		
	}
	static class XCell extends ListCell<Preference> {
		public HBox hbox = new HBox();
		public VBox vbox = new VBox();
				
		Label label = new Label("(empty)");
		Pane pane = new Pane();
		Button editButton = new Button();
		Button deleteButton = new Button();
		
		Preference lastItem;
		
		public XCell(){ 
			super();
		
			//DropShadow shadow = new DropShadow();
			
			Image imageDelete = new Image(Main.class.getResourceAsStream("images/delete.png"));
			Image imageEdit = new Image(Main.class.getResourceAsStream("images/edit.png"));
			
			deleteButton.setGraphic(new ImageView(imageDelete));
			deleteButton.setBackground(null);
			
			editButton.setGraphic(new ImageView(imageEdit));
			editButton.setBackground(null);
			/*editButton.setEffect(shadow);
			
			editButton.addEventHandler(MouseEvent.MOUSE_ENTERED, 
				    new EventHandler<MouseEvent>() {
				        @Override public void handle(MouseEvent e) {
				        	//editButton.setEffect(shadow);
				        	editButton.setEffect(null);
				        }
				});
			editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, 
					new EventHandler<MouseEvent>(){
						@Override public void handle(MouseEvent e) {
							editButton.setEffect(null);
						}
				});*/
			
			hbox.getChildren().addAll(label, pane, editButton, deleteButton);
			HBox.setHgrow(pane, Priority.ALWAYS);
			vbox.getChildren().add(hbox);
		}
		
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
							System.out.println(item.getWeight());
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
