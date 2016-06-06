package br.uece.goes.model;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import com.sun.xml.internal.ws.util.StringUtils;

import br.uece.goes.controller.instance.SpreadSheetInstance;
import javafx.collections.ObservableList;
import javafx.scene.control.TablePosition;

public class FormatAndPasteFromClipBoard {

	
	public static void format(SpreadSheetInstance p){
		long time  = System.currentTimeMillis();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		String result = null;
		Transferable t = clipboard.getContents(null);
        
		 if (!t.isDataFlavorSupported(DataFlavor.stringFlavor))
		   	  return;
		
		try {
			result = (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		
		if(result == null) return;
		
		String [] lines = result.split("\\r?\\n");
			
		int row = p.getSelectionModel().getSelectedCells().get(0).getRow();
		int column = p.getSelectionModel().getSelectedCells().get(0).getColumn();
		
		
		for (int i = 0; i < lines.length; i++) {
			
			String [] line = lines[i].split("\\t+");
			
			if(row+i >= p.getGrid().getRowCount())
				break;
			
			for (int j = 0; j < line.length; j++) {
				if(column+j >= p.getGrid().getColumnCount())
					break;
				
				SpreadsheetCell cell = p.getCell(i+row, j+column);
				
				if(cell == null) continue;
				
				String ct = cell.getCellType().toString();
				
				if(ct.equals("double")) {
					if(isNumeric(line[j])){
						cell.setItem(Double.parseDouble(line[j]));
					}
				}else if(ct.equals("Integer")) {
					if(isNumeric(line[j])){
						cell.setItem(Integer.parseInt(line[j]));
					}
				}else {
					cell.setItem(line[j]);
				}
			}
		}
		
		System.out.println(System.currentTimeMillis() - time);
	}
	
	public static void pasteToClipBoard(SpreadSheetInstance p) {
		/*long time = System.currentTimeMillis();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		ObservableList<TablePosition> selectedCell = p.getSelectionModel().getSelectedCells();
		int currentRow = selectedCell.get(0).getRow();
		//int column = tablePosition.getColumn();
		String aux = "";
		
		for (TablePosition tablePosition : selectedCell) {
			int row = tablePosition.getRow();
			int column = tablePosition.getColumn();
			
			aux+=p.getCell(row, column).getItem()+"\t";
			
			if(row != currentRow) {
				aux += "\n";
				currentRow = row;
			}
			
			clipboard.setContents(new StringSelection(aux), null);
		}
						
		System.out.println(System.currentTimeMillis() - time);*/
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
