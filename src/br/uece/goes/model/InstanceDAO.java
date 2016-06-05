package br.uece.goes.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class InstanceDAO {
	
	public boolean  saveInstance(Instance instance) {
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        File file = fileChooser.showSaveDialog(new Stage());
        
        if (file != null) {
            try {
				writeInstance(instance, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return true;
        }
        
        return false;
	}
	
	public void writeInstance(Instance instance, File file) throws IOException {
		String endOfLine = "\n";
		FileWriter fw = new FileWriter(file);
		fw.write(instance.getnOfRequirements()+" "+instance.getnOfClients()+endOfLine);
		for (int i = 0; i < instance.getnOfClients(); i++) {
			fw.write(instance.getClientWeight()[i]+" ");
		}
		
		fw.write(endOfLine);
		
		for (int i = 0; i < instance.getnOfRequirements(); i++) {
			fw.write(instance.getCost()[i]+" ");
			fw.write(instance.getRisk()[i]+endOfLine);
		}
		
		for (int i = 0; i < instance.getnOfClients(); i++) {
			for (int j = 0; j < instance.getnOfRequirements(); j++) {
				fw.write(instance.getScore()[i][j]+" ");
			}
			fw.write(endOfLine);
		}
		
		for (int i = 0; i < instance.getnOfRequirements(); i++) {
			for (int j = 0; j < instance.getnOfRequirements(); j++) {
				fw.write(instance.getPrecedence()[i][j]+" ");
			}
			fw.write(endOfLine);
		}
		
		for (int i = 0; i < instance.getnOfRequirements(); i++) {
			fw.write(instance.getDescriptions()[i]+endOfLine);
		}
		
		fw.close();
		
	}
}
