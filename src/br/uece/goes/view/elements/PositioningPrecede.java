package br.uece.goes.view.elements;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import jmetal.interactive.core.Preference;
import jmetal.interactive.core.PreferenceFactory;
import jmetal.problems.ReleasePlanningProblem;

public class PositioningPrecede extends Window{

	public PositioningPrecede(ReleasePlanningProblem rpp, Stage stage) {
		super(rpp, stage);
		stage.setTitle("Add New Positioning Precede");
		PreferencesComboBox comb1 = new PreferencesComboBox(rpp.getReqDescriptions());
		PreferencesComboBox comb2 = new PreferencesComboBox(rpp.getReqDescriptions());
		PreferencesComboBox comb3 = new PreferencesComboBox(rpp.getReleases());
		GridPaneLayout fild1 = 
				new GridPaneLayout("Requirement 1:", comb1);
		GridPaneLayout fild2 = 
				new GridPaneLayout("Requirement 2:", comb2);
		GridPaneLayout fild3 = new GridPaneLayout("Distance: ", comb3);
		controller.vbox.getChildren().addAll(GROW_PANE_1, fild1, fild2, fild3, GROW_PANE_2);
		controller.vbox.setVgrow(GROW_PANE_1, Priority.ALWAYS);
		controller.vbox.setVgrow(GROW_PANE_2, Priority.ALWAYS);
						
		controller.button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				int weight = (int) Math.round(controller.weightSlider.getValue());
				Preference pref = 
						PreferenceFactory.makePreference("positioning_precede", 
								""+comb1.selectedIndex()+" "+comb2.selectedIndex()+" "+
						(comb3.selectedIndex()+1), weight);
				addPreference(pref);
			}
		});	}

}
