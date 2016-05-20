package br.uece.goes.controller.config;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;
import jmetal.core.Algorithm;
import jmetal.problems.ReleasePlanningProblem;

public class Settings {
	@FXML
	TextField weight;
	
	@FXML
	Button delete;
	
	@FXML
	Button add;
	
	@FXML
	Button apply;
	
	@FXML
	ComboBox<String> algorithm;
	
	@FXML
	TableView<ReleaseModel> table;
	
	// Items for which configurations are applied
	Algorithm algorihtm;
	
	ReleasePlanningProblem problem;

	public final double DEFAULT_WEIGHT = 1;
	private Stage scene;

	@FXML
	void initialize() {
		
		table.setItems(FXCollections.observableArrayList());
		
		// Delete Function
		delete.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				int item = table.getSelectionModel().getSelectedIndex();
				if(item < 0) return;
				table.getItems().remove(item);
				for (int i = item; i < table.getItems().size(); i++) {
					table.getItems().get(i).setIndex(i+1);
				}
			}
		});
		// Add function
		add.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				table.getItems().add(new ReleaseModel(table.getItems().size()+1, 0));	
			}
		});
		
		// Apply Function
		apply.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				apply();
			}
		});
		
		//Configuration of values for column 1
		TableColumn<ReleaseModel, Integer> indexColumn = new TableColumn<Settings.ReleaseModel, Integer>("Release #");
		indexColumn.setCellValueFactory(new PropertyValueFactory<ReleaseModel, Integer>("index"));
				
		//Configuration of values for clumn 2
		TableColumn<ReleaseModel, Integer> budgetColumn = new TableColumn<Settings.ReleaseModel, Integer>("Budget");
		budgetColumn.setCellValueFactory(new PropertyValueFactory<ReleaseModel, Integer>("value"));
		budgetColumn.setEditable(true);
		budgetColumn.setCellFactory(TextFieldTableCell.<ReleaseModel, Integer>forTableColumn(new IntegerStringConverter()));
		budgetColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ReleaseModel,Integer>>() {

			@Override
			public void handle(CellEditEvent<ReleaseModel, Integer> event) {
				int index = event.getTablePosition().getRow();
				table.getItems().get(index).setValue(event.getNewValue());
				if(table.getItems().size() != index) {
					table.getSelectionModel().selectBelowCell();
					table.edit(index+1, budgetColumn);
				}
			}
		});
		table.setEditable(true);
		table.getColumns().addAll(indexColumn, budgetColumn);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getItems().add(new ReleaseModel(1, 0));
		weight.setText(""+DEFAULT_WEIGHT);
	}
	
	public void setProblem(ReleasePlanningProblem problem) {
		this.problem = problem;
		
	}
	
	public void update() {
		weight.setText(""+problem.getAlpha());
		int i = 1;
		for (ReleaseModel release : table.getItems()) {
			release.setValue(problem.getBudget(i++));
		}
	}
	
	public void apply() {	
		problem.setAlpha(Double.parseDouble(weight.getText()));
		Integer []releaseCost = new Integer[table.getItems().size()];
		
		for (int i = 0; i < releaseCost.length; i++) {
			releaseCost[i] = table.getItems().get(i).getValue();
		}
		problem.setReleaseCost(releaseCost);
		scene.close();
	}
	
	public void resetValues() {
		table.getItems().clear();
		table.getItems().add(new ReleaseModel(1, 0));
		weight.setText(""+DEFAULT_WEIGHT);
	}
	
	public static class ReleaseModel {
		public SimpleIntegerProperty index;
		public SimpleIntegerProperty value;

		public ReleaseModel(Integer index, Integer value) {
			this.index = new SimpleIntegerProperty(index);
			this.value = new SimpleIntegerProperty(value);
		}

		public Integer getIndex() {
			return index.get();
		}

		public void setIndex(Integer index) {
			this.index.setValue(index);
		}

		public Integer getValue() {
			return value.get();
		}

		public void setValue(Integer value) {
			this.value.setValue(value);;
		}

		
		
		
	}

	public void setScene(Stage s) {
		this.scene = s;
		
	}

}
