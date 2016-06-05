package br.uece.goes.controller.instance;

import java.io.IOException;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import br.uece.goes.controller.config.Settings.ReleaseModel;
import br.uece.goes.controller.instance.InstanceCreatorSettingsController.Client;
import br.uece.goes.view.Main;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;

public class InstanceCreatorSettingsController {

	@FXML
	TextField nOfReqField;
	
	@FXML
	Button deleteClient;
	
	@FXML
	Button addClient;

	@FXML
	Button applyButton;
	
	@FXML
	Button cancelButton;

	@FXML
	TableView<Client> clientTable;

	ObservableList<Client> listOfClientes;
	
	@FXML
	BorderPane pane;
	
	SpreadSheetInstance spreadsheet;
	
	@FXML
	void initialize() {
		
		
		TableColumn indexCol = new TableColumn<Client, Integer>("Client #");
		indexCol.setCellValueFactory(new PropertyValueFactory<Client, Integer>("index"));
		
		TableColumn <Client, Integer> clientWeight= new TableColumn<Client, Integer>("Weight");
		clientWeight.setCellValueFactory(new PropertyValueFactory<Client, Integer>("weight"));
		clientWeight.setCellFactory(TextFieldTableCell.<Client, Integer>forTableColumn(new IntegerStringConverter()));
		
		clientWeight.setEditable(true);
		clientWeight.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Client,Integer>>() {
			
			@Override
			public void handle(CellEditEvent<Client, Integer> event) {
				int index = event.getTablePosition().getRow();
				clientTable.getItems().get(index).setWeight(event.getNewValue());
				if(clientTable.getItems().size() != index) {
					clientTable.getSelectionModel().selectBelowCell();
					clientTable.edit(index+1, clientWeight);
				}
			}
		});
		
		addClient();		
		clientTable.getColumns().add(indexCol);
		clientTable.getColumns().add(clientWeight);
		clientTable.setEditable(true);
		listOfClientes = clientTable.getItems();
		
		configButtons();
	}
	
	private static InstanceCreatorSettingsController icsc = null;
	
	private InstanceCreatorSettingsController() {
		
	}
	
	public static InstanceCreatorSettingsController getInstance() {
		
		if(icsc == null) {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("InstanceSettings.fxml"));
			icsc = new InstanceCreatorSettingsController();
			loader.setController(icsc);
			try {
				loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return icsc;
	}
	
	void configButtons() {
		addClient.setText("");
		addClient.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.PLUS));
		addClient.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				addClient();
				if(spreadsheet != null) {
					spreadsheet.addClient();
				}
			}
		});
		
		deleteClient.setText("");
		deleteClient.setGraphic(new Glyph("FontAwesome", FontAwesome.Glyph.MINUS));
		deleteClient.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				int index = clientTable.getSelectionModel().getSelectedIndex();
				if(spreadsheet != null) {
					spreadsheet.deleteClient(index+spreadsheet.NUMBER_OF_ATTRIBUTES);
				}
			}
		});
		
	}
	
	public void addClient() {
		int lastPos = clientTable.getItems().size();
		clientTable.getItems().add(new Client(lastPos, 0));
	}
	
	public void deleteClient(int index) {
		clientTable.getItems().remove(index);
		for (int row = index; row < clientTable.getItems().size(); row++) {
			clientTable.getItems().get(row).setIndex(row+1);
		}
		spreadsheet.deleteClient(index);
	}
	
	public class Client {
		
		SimpleIntegerProperty index;
		SimpleIntegerProperty weight;
		
		public Client(int index, int weight) {
			this.index = new SimpleIntegerProperty(index);
			this.weight = new SimpleIntegerProperty(weight);
		}
		
		public Integer getIndex() {
			return index.get();
		}

		public void setIndex(Integer index) {
			this.index.set(index);
		}

		public void setWeight(Integer weight) {
			this.weight.set(weight);;
		}
		
		public Integer getWeight() {
			return this.weight.getValue();
		}
		
		
	}

	public Button getApplyButton() {
		return applyButton;
	}
	
	public BorderPane getPane() {
		return pane;
	}
	
	public int getNumberOfRequirements() {
		return Integer.parseInt(nOfReqField.getText());
	}
	
	public int [] getClientWeights() {
		int [] weights = new int[listOfClientes.size()];
		
		for (int i = 0; i < weights.length; i++) {
			weights[i] = listOfClientes.get(i).getWeight();
		}
		
		return weights;
		
	}

	public TableView<Client> getClientTable() {
		return clientTable;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public SpreadSheetInstance getSpreadsheet() {
		return spreadsheet;
	}

	public void setSpreadsheet(SpreadSheetInstance spreadsheet) {
		this.spreadsheet = spreadsheet;
	}

	public TextField getnOfReqField() {
		return nOfReqField;
	}
}
