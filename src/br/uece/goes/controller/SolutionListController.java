package br.uece.goes.controller;

import java.util.ArrayList;

import br.uece.goes.controller.PreferenceListController.XCell;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.interactive.core.Preference;
import jmetal.problems.Osyczka2;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;

public class SolutionListController{
	
	private static int nReleases;
	
	private ListView<TableView<String>> solutionView;
	
	public static ObservableList<TableView<String>> releases;  

	static String [] reqDescriptions;
	
	SolutionListController(ListView solutionView) {
		this.solutionView = solutionView;
		
		solutionView.setCellFactory(new Callback<ListView<TableView<String>>, ListCell<TableView<String>>>() {
			
			@Override
			public ListCell<TableView<String>> call(ListView<TableView<String>> param) {
				// TODO Auto-generated method stub
				return new TableCell();			
			}
		});
	}
	
	public void createTables(ReleasePlanningProblem problem) {
		nReleases = problem.getReleases();
		reqDescriptions = problem.getReqDescriptions();
		releases = FXCollections.observableArrayList();
		solutionView.setItems(releases);		
		TableView<String> aux;
		for (int i = 0; i < problem.getReleases(); i++) {
		
			aux = new TableView<String>();
			releases.add(aux);
			
			TableColumn<String, String> column = new TableColumn<String, String>("Release "+(i+1));		
			column.setCellValueFactory((p)->{
				return new ReadOnlyStringWrapper(p.getValue());
			});			
			
			aux.getColumns().add(column);
			aux.setItems(FXCollections.observableArrayList());
			aux.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		}
		
		aux = new TableView<String>();
		releases.add(aux);
		TableColumn<String, String> column = new TableColumn<String, String>("Not Allocated");		
		column.setCellValueFactory((p)->{
			return new ReadOnlyStringWrapper(p.getValue());
		});	
		
		aux.getColumns().add(column);
		aux.setItems(FXCollections.observableArrayList());
		aux.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
	}
		
	public static void updateSolutionViewer(Solution solution) throws JMException {

		Variable[] variables = solution.getDecisionVariables();
		for (TableView<String> p: releases) {
			p.getItems().clear();
		}
		for (int i = 0; i < variables.length; i++) {
			int index = (int)variables[i].getValue()-1;
			if(index < 0) index = nReleases;
			releases.get(index).getItems().add(reqDescriptions[i]);
		}
		
		
	}
	
	static class TableCell extends ListCell<TableView<String>> {
		TableView<String> lastItem;
		
		public TableCell() {			
			super();
		}
		
		@Override
		protected void updateItem(TableView<String> item, boolean empty) {
			super.updateItem(item, empty);
			setText(null);  // No text in label of super class
			if (empty) {
				lastItem = null;
				setGraphic(null);
			} else {
				lastItem = item;
				item.setMinWidth(400);
				setGraphic(item);
			}
		}
	}
		
}
