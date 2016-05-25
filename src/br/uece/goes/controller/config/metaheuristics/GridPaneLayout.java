package br.uece.goes.controller.config.metaheuristics;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GridPaneLayout extends GridPane{
	
	Node node;
	
	public GridPaneLayout(String label, Node node) {
		this.node = node;
		add(new Label(label), 0, 0);
		add(node, 1, 0);		
		getColumnConstraints().add(new ColumnConstraints(200));
	    getColumnConstraints().add(new ColumnConstraints(390));
	    setPadding(new Insets(5, 0, 5, 0));
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
