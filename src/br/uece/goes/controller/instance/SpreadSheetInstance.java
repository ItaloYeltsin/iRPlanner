package br.uece.goes.controller.instance;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TablePosition;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import br.uece.goes.model.FormatAndPasteFromClipBoard;

public class SpreadSheetInstance extends SpreadsheetView {
	int nOfReq;

	int nOfClientes;

	public final int NUMBER_OF_ATTRIBUTES = 4;

	ArrayList<String> headers;

	ObservableList<String> items = FXCollections.observableArrayList();

	ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections
			.observableArrayList();

	public SpreadSheetInstance(int nOfReq, int nOfClientes) {
		this.nOfClientes = nOfClientes;
		this.nOfReq = nOfReq;

		int rowCount = nOfReq;
		int columnCount = nOfClientes + NUMBER_OF_ATTRIBUTES;

		GridBase grid = new GridBase(rowCount, columnCount);

		// Setting Column headers
		headers = new ArrayList<>();
		headers.add("Description");
		headers.add("Cost");
		headers.add("Risk");
		headers.add("Precedence");

		for (int column = NUMBER_OF_ATTRIBUTES; column < grid.getColumnCount(); column++) {
			headers.add("Client " + (column - 3));
		}

		for (int i = 0; i < nOfReq; i++) {
			items.add("" + (i + 1));
		}

		for (int row = 0; row < grid.getRowCount(); ++row) {
			final ObservableList<SpreadsheetCell> list = FXCollections
					.observableArrayList();
			// Description Column Cells
			list.add(SpreadsheetCellType.STRING.createCell(row, 0, 1, 1, ""));
			// Cost Column Cells
			list.add(SpreadsheetCellType.INTEGER.createCell(row, 1, 1, 1, 0));
			// Risk Column Cells
			list.add(SpreadsheetCellType.INTEGER.createCell(row, 2, 1, 1, 0));
			SpreadsheetCell p = SpreadsheetCellType.STRING.createCell(row, 3,
					1, 1, "");
			CheckComboBox ccb = new CheckComboBox<String>(items);
			p.setGraphic(ccb);
			list.add(p);

			for (int column = NUMBER_OF_ATTRIBUTES; column < grid
					.getColumnCount(); ++column) {
				list.add(SpreadsheetCellType.INTEGER.createCell(row, column, 1,
						1, 0));
			}
			rows.add(list);
		}

		grid.setRows(rows);

		grid.getColumnHeaders().setAll(headers);

		this.setGrid(grid);
		SpreadSheetInstance si = this;
		this.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				KeyCombination kb = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
				
				if(kb.match(event)) {
					FormatAndPasteFromClipBoard.format(si);
					return;
				}
				
				kb = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
				if(kb.match(event)) {
					FormatAndPasteFromClipBoard.pasteToClipBoard(si);
				}
			}
		});
	}

	/**
	 * Add a client into the Spreadsheet
	 */
	public void addClient() {

		nOfClientes++;
		int i = 0;
		for (ObservableList<SpreadsheetCell> list : rows) {

			list.add(SpreadsheetCellType.INTEGER.createCell(i++,
					NUMBER_OF_ATTRIBUTES + nOfClientes - 1, 1, 1, 0));
		}
		GridBase gd = new GridBase(nOfReq, nOfClientes + NUMBER_OF_ATTRIBUTES);

		headers.add("Client " + nOfClientes);
		gd.setRows(rows);
		gd.getColumnHeaders().setAll(headers);
		setGrid(gd);

	}

	/**
	 * Delete a client from the Spreadsheet
	 */
	public void deleteClient(int column) {
		
		if (column < NUMBER_OF_ATTRIBUTES) {
			throw new IllegalArgumentException("This is not a client column");
		}

		nOfClientes--;

		int counter = 0;
		for (ObservableList<SpreadsheetCell> observableList : rows) {
			for (int i = column + 1; i < observableList.size(); i++) {
				observableList.set(i, SpreadsheetCellType.INTEGER.createCell(
						counter, i - 1, 1, 1, (Integer) observableList.get(i)
								.getItem()));
			}
			counter++;
			observableList.remove(column);
		}

		headers.remove(column);

		for (int i = NUMBER_OF_ATTRIBUTES; i < headers.size(); i++) {
			headers.set(i, "Client " + (i - (NUMBER_OF_ATTRIBUTES - 1)));
		}

		GridBase gd = new GridBase(nOfReq, nOfClientes + NUMBER_OF_ATTRIBUTES);
		gd.setRows(rows);
		gd.getColumnHeaders().setAll(headers);
		setGrid(gd);

	}

	/**
	 * Add one requirement to the last position of the Spreadsheet
	 */
	public void addRequirement() {
		ObservableList<SpreadsheetCell> list = FXCollections
				.observableArrayList();
		nOfReq++;
		GridBase gb = new GridBase(nOfReq, NUMBER_OF_ATTRIBUTES + nOfClientes);

		items.add("" + (items.size() + 1));

		list.add(SpreadsheetCellType.STRING.createCell(rows.size(), 0, 1, 1, ""));
		// Cost Column Cells
		list.add(SpreadsheetCellType.INTEGER.createCell(rows.size(), 1, 1, 1,
				0));
		// Risk Column Cells
		list.add(SpreadsheetCellType.INTEGER.createCell(rows.size(), 2, 1, 1,
				0));
		SpreadsheetCell p = SpreadsheetCellType.STRING.createCell(rows.size(),
				3, 1, 1, "");
		CheckComboBox<String> ccb = new CheckComboBox<String>(items);
		p.setGraphic(ccb);
		list.add(p);

		for (int column = NUMBER_OF_ATTRIBUTES; column < gb.getColumnCount(); ++column) {
			list.add(SpreadsheetCellType.INTEGER.createCell(rows.size(), column,
					1, 1, 0));
		}

		rows.add(list);
		gb.setRows(rows);
		gb.getColumnHeaders().setAll(headers);
		setGrid(gb);
	}
	
	/**
	 * Delete a Row/Requirement from the Spreadsheet
	 */
	public void deleteRequirement() {
		int row = getSelectionModel().getSelectedCells().get(0).getRow();
		ObservableList<TablePosition> selectedCells = getSelectionModel()
				.getSelectedCells();
		
		GridBase gb = new GridBase(++nOfReq, NUMBER_OF_ATTRIBUTES + nOfClientes);
		
		for (TablePosition tp : selectedCells) {
			int current = tp.getRow();
			if (row - current != 0) {
				throw new IllegalArgumentException(
						"Select only one requirement to be deleted");
			}
		}
		
		int lastItem = items.size()-1;
		items.remove(lastItem);
		nOfReq--;

		rows.remove(row);

		for (int i = row; i < rows.size(); i++) {
			ObservableList<SpreadsheetCell> list = rows.get(i);

			list.set(0, SpreadsheetCellType.STRING.createCell(i, 0, 1,
					1, (String)list.get(0).getItem()));
			// Cost Column Cells
			list.set(1, SpreadsheetCellType.INTEGER.createCell(i, 1, 1,
					1, (Integer)list.get(1).getItem()));
			// Risk Column Cells
			list.set(2, SpreadsheetCellType.INTEGER.createCell(i, 2, 1,
					1, (Integer)list.get(2).getItem()));
			// Precedence Cells
			SpreadsheetCell p = SpreadsheetCellType.STRING.createCell(
					i, 3, 1, 1, "");
			p.setGraphic(list.get(3).getGraphic());
			list.set(3, p);

			for (int column = NUMBER_OF_ATTRIBUTES; column < gb.getColumnCount(); ++column) {
				list.set(column, SpreadsheetCellType.INTEGER.createCell(i,
						column, 1, 1, (Integer)list.get(column).getItem()));
			}

		}

		gb.setRows(rows);
		gb.getColumnHeaders().setAll(items);
		setGrid(gb);

	}
	
	public SpreadsheetCell getCell(int i, int j) {	
		if(rows.size() <= i) {
			return null;
		} else if(rows.get(i).size() <= j) {
			return null;
		}
		
		return rows.get(i).get(j);		
	}
}
