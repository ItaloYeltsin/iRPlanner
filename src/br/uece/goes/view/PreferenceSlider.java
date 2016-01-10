package br.uece.goes.view;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import jmetal.interactive.core.Preference;

public class PreferenceSlider extends Alert{
	
	Preference preference;
	Slider slider;
	
	public PreferenceSlider(double x, double y, Preference preference) {
		super(AlertType.NONE);
		this.preference = preference;
		this.setX(x);
		this.setY(y);
		this.initStyle(StageStyle.UNDECORATED);
		slider = new Slider(1, 10, preference.getWeight());
		slider.setOrientation(Orientation.VERTICAL);
		DialogPane dp = getDialogPane();
		dp.setContent(slider);
		
		dp.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				double x = event.getX();
				double y = event.getY();
				System.out.println(x+ " "+y);
				if(x < 0 || x > dp.getHeight() || y < 0 || y > dp.getHeight() ) {
					preference.setWeight((int)slider.getValue());
					close();
				}
					
			}
		});
		show();
	}
	


}
