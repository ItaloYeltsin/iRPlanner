package br.uece.goes.view.elements;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GridPaneLayout extends GridPane{
	public GridPaneLayout(String label, Node node) {
		add(new Label(label), 0, 0);
		add(node, 1, 0);
		getColumnConstraints().add(new ColumnConstraints(100));
	    getColumnConstraints().add(new ColumnConstraints(400));
	    setPadding(new Insets(5, 0, 5, 0));
	}
}
