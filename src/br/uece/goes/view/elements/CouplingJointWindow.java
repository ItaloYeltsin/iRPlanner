package br.uece.goes.view.elements;

import br.uece.goes.controller.PreferenceListController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import jmetal.interactive.core.Preference;
import jmetal.interactive.core.PreferenceFactory;
import jmetal.problems.ReleasePlanningProblem;

public class CouplingJointWindow extends Window{
	
	public CouplingJointWindow(ReleasePlanningProblem rpp, Stage stage) {
		super(rpp, stage);
		stage.setTitle("Add New Coupling Joint");
		PreferencesComboBox comb1 = new PreferencesComboBox(rpp.getReqDescriptions());
		PreferencesComboBox comb2 = new PreferencesComboBox(rpp.getReqDescriptions());
		GridPaneLayout fild1 = 
				new GridPaneLayout("Requirement 1:", comb1);
		GridPaneLayout fild2 = 
				new GridPaneLayout("Requirement 2:", comb2);
		controller.vbox.getChildren().addAll(GROW_PANE_1, fild1, fild2, GROW_PANE_2);
		controller.vbox.setVgrow(GROW_PANE_1, Priority.ALWAYS);
		controller.vbox.setVgrow(GROW_PANE_2, Priority.ALWAYS);
						
		controller.button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				int weight = (int) Math.round(controller.weightSlider.getValue());
				Preference pref = 
						PreferenceFactory.makePreference("coupling_joint", 
								""+comb1.selectedIndex()+" "+comb2.selectedIndex(), weight);
				addPreference(pref);
			}
		});
	}
}
