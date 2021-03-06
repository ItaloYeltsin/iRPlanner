package br.uece.goes.controller.instance;


import java.io.IOException;

import javax.print.attribute.standard.DialogTypeSelection;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import br.uece.goes.controller.MainController;
import br.uece.goes.model.Instance;
import br.uece.goes.model.InstanceDAO;
import br.uece.goes.view.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TablePosition;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
/**
 * This class is the controller of the Instance Editor
 * @author italo
 * 
 */
public class InstanceCreatorController {
	
	SpreadSheetInstance spreadsheetView;
	
	@FXML
	ToolBar toolbar;

	@FXML
	Button newInstance;
	
	@FXML
	Button save;
	
	@FXML
	Button addCliente;
	
	@FXML
	Button deleteCliente;
	
	@FXML
	Button addReq;
	
	@FXML
	Button deleteReq;
	
	@FXML
	Button close;
	
	@FXML
	Button configButton;

	@FXML
	BorderPane pane;
	
	//self instance
	static InstanceCreatorController icc;
	
	static MainController mainController;
	
	InstanceCreatorSettingsController icsc;
	Stage settingsStage;
	
	
	boolean altered = false;
	
	boolean cancel = false;
		
	private InstanceCreatorController(MainController mainController) {
		this.mainController = mainController;
		icsc = InstanceCreatorSettingsController.getInstance();
		
	}
	
	/**
	 * return the only instance of InstanceCreatorController.
	 * @param mainController
	 * @return
	 */
	public static InstanceCreatorController getInstance(MainController mainController) {
		
		if(icc == null) {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource(
					"InstanceEditorView.fxml"));
			icc = new InstanceCreatorController(mainController);
			loader.setController(icc);
			try {
				loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return icc;
	}
	
	/**
	 * Function that is called after the auto injection
	 */
	@FXML
	void initialize() {
		configButtons();
		this.spreadsheetView = new SpreadSheetInstance(10, 3);
		settingsStage = new Stage();
		settingsStage.setAlwaysOnTop(true);
		settingsStage.setScene(new Scene(icsc.getPane()));
		
	}
	
	public boolean createNewInstance() {
		cancel = false;
		settingsStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				cancel = true;
				settingsStage.close();
			}
			
		});
		
		icsc.getApplyButton().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				settingsStage.close();
			} 
		});
		
		icsc.setSpreadsheet(null);
		settingsStage.showAndWait();
		
		if(cancel == true) {
			return true;
		}
		
		
		int nOfReq = icsc.getNumberOfRequirements();
		int nOfClients = icsc.getClientTable().getItems().size();
		
		spreadsheetView = new SpreadSheetInstance(nOfReq, nOfClients);
		pane.setCenter(spreadsheetView);
		icsc.setSpreadsheet(spreadsheetView);
		
		return false;
	}
	
	/**
	 * change the content of the main pane in the class MainController
	 * @param node
	 */
	void changeMainNode(Node node) {
		mainController.getAnchorpane().getChildren().clear();
		mainController.getAnchorpane().getChildren().add(node);
		mainController.getAnchorpane().setTopAnchor(node, 0.0);
		mainController.getAnchorpane().setLeftAnchor(node, 0.0);
		mainController.getAnchorpane().setRightAnchor(node, 0.0);
		mainController.getAnchorpane().setBottomAnchor(node, 0.0);
	}
		
	/**
	 * Set the button icon and style
	 * @param button
	 * @param glyph
	 */
	void configButtonStyle(Button button, org.controlsfx.glyphfont.FontAwesome.Glyph glyph, String toolTip) {
		button.setText("");
		button.setBackground(null);
		Glyph g = new Glyph("FontAwesome", glyph);
		button.setGraphic(g);
		button.setTooltip(new Tooltip(toolTip));
		
		DropShadow shadow = new DropShadow();
		shadow.setColor(Color.color(0.01, 0.61, 0.82));
		
		button.setOnMouseEntered(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				button.setEffect(shadow);
			}
		});
		
		button.setOnMouseExited(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				button.setEffect(null);
			}
		});
	}
	
	/**
	 * This function configure the style and functionalities of all buttons;
	 */
	void configButtons() {
		configButtonStyle(newInstance, FontAwesome.Glyph.FILE_ALT, "New instance");
		configButtonStyle(save, FontAwesome.Glyph.SAVE, "Save");
		configButtonStyle(addReq, FontAwesome.Glyph.PLUS, "Add requirement");
		configButtonStyle(deleteReq, FontAwesome.Glyph.MINUS, "Delete requirement");
		configButtonStyle(addCliente, FontAwesome.Glyph.USER_PLUS, "Add client");
		configButtonStyle(deleteCliente, FontAwesome.Glyph.USER_TIMES, "Delete cliente");
		configButtonStyle(configButton, FontAwesome.Glyph.GEAR, "Instance Settings");
		configButtonStyle(close, FontAwesome.Glyph.CLOSE, "Close instance creator");
		
		mainController.getInstanceCreatorButton().setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(createNewInstance()) return;
				changeMainNode(pane);
			}
		});
		
		close.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				changeMainNode(mainController.getContent());			
			}
		});
		
		addCliente.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				icsc.addClient();
			}
		});
		
		deleteCliente.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				int column = spreadsheetView.getSelectionModel().getSelectedCells().get(0).getColumn();
				ObservableList<TablePosition> selectedCells = spreadsheetView.getSelectionModel()
						.getSelectedCells();

				for (TablePosition tp : selectedCells) {
					int current = tp.getColumn();
					if (column - current != 0) {
						throw new IllegalArgumentException(
								"Select only one client column to be deleted");
					}
				}
				
				icsc.deleteClient(column);
				//spreadsheetView.deleteClient(column);
			}
		});
		
		addReq.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				spreadsheetView.addRequirement();
			}
		});
		
		deleteReq.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				spreadsheetView.deleteRequirement();
			}
		});
		
		newInstance.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				createNewInstance();
			}
		});
		
		configButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				icsc.getnOfReqField().setDisable(true);
				settingsStage.showAndWait();
				icsc.getnOfReqField().setDisable(false);
			}
		});
		
		save.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				int nOfClients = spreadsheetView.nOfClientes;
				int nOfRequirements = spreadsheetView.nOfReq;
				Instance instance = new Instance(nOfClients, nOfRequirements);
				instance.setClientWeight(icsc.getClientWeights());
				instance.setDescriptions(getStringArray(0));
				instance.setCost(getIntArray(1));
				instance.setRisk(getIntArray(2));
				instance.setPrecedence(getPrecedences());
				instance.setScore(getClientScores());
				
				boolean altered = new InstanceDAO().saveInstance(instance);
				
			}
		});
		
	} // configButtons
	
	public int [] getIntArray(int column) {
		int size = spreadsheetView.nOfReq;
		
		int [] vet = new int[size];
		
		for (int i = 0; i < vet.length; i++) {
			vet[i] = (Integer)spreadsheetView.getCell(i, column).getItem();
		}
		
		return vet;
		
		
	}
	
	public String [] getStringArray(int column) {
		int size = spreadsheetView.nOfReq;
		
		String [] vet = new String[size];
		
		for (int i = 0; i < vet.length; i++) {
			vet[i] = (String)spreadsheetView.getCell(i, column).getItem();
		}
		
		return vet;
		
		
	}
	
	public int [][] getPrecedences() {
		int size = spreadsheetView.nOfReq;
			
		int [][] precedences = new int[size][size];
		for (int i = 0; i < precedences.length; i++) {
			CheckComboBox<String> ccbBox = 
					(CheckComboBox<String>) spreadsheetView.getCell(i, 3).getGraphic();
			ObservableList<Integer> si = ccbBox.getCheckModel().getCheckedIndices();
			for (Integer integer : si) {
				precedences[i][integer] = 1;
			}
		}
		
		return precedences;	
		
	}
	
	public int [] [] getClientScores() {
		int [][] matrix = new int [spreadsheetView.nOfClientes][spreadsheetView.nOfReq];
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = 
						(Integer)spreadsheetView.
						getCell(j, i+spreadsheetView.NUMBER_OF_ATTRIBUTES).getItem();
			}
		}
		return matrix;
	}
	
}
