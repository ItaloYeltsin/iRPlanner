package br.uece.goes.view.elements;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class PreferencesComboBox extends ComboBox{
	public PreferencesComboBox(String [] descriptions) {
		this.setItems(FXCollections.observableArrayList(descriptions));
	}
	
	public PreferencesComboBox(int n) {
		String [] releases = new String[n];
		for (int i = 0; i < n; i++) {
			releases[i] = ""+(i+1);
		}
		this.setItems(FXCollections.observableArrayList(releases));
	}
	public PreferencesComboBox(String prefix, int n) {
		String [] releases = new String[n];
		for (int i = 0; i < n; i++) {
			releases[i] = prefix+" "+(i+1);
		}
		this.setItems(FXCollections.observableArrayList(releases));
	}
	public int selectedIndex() {
		Object object = getValue();
		for (int i = 0; i < getItems().size(); i++) {
			if(object.equals(getItems().get(i))){
				return i;
			}
		}
		return -1;
	}

}
