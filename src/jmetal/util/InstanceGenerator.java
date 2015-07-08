package jmetal.util;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

public class InstanceGenerator {

	private int numberOfCustomers;
	private int numberOfRequirements;
	private int numberOfReleases;
	private int riskUpperBound;
	private int importanceUpperBound;
	private int costUpperBound;
	private Random random;
	
	public InstanceGenerator(int numberOfCustomers, int numberOfRequirements, int numberOfReleases,
			int importanceUpperBound, int costUpperBound, int riskUpperBound){
		this.numberOfCustomers = numberOfCustomers;
		this.numberOfRequirements = numberOfRequirements;
		this.importanceUpperBound = importanceUpperBound;
		this.costUpperBound = costUpperBound;
		this.riskUpperBound = riskUpperBound;
		this.numberOfReleases = numberOfReleases;
		this.random = new Random();
	}
	
	public void generateInstance(){
		
		try{
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(
							new File("in/I_"+numberOfRequirements+".rp")));
			//<number of releases> <number of requirements> <number of clients>
			writer.write(numberOfReleases+" "+numberOfRequirements+" "+numberOfCustomers + "\n");
			writer.write("\n");
			
			//<importance of each client>
			int[] costumersImportance = getIntVector(importanceUpperBound, numberOfCustomers);
			for (int i = 0; i < costumersImportance.length; i++) {
				writer.write(costumersImportance[i]+" ");
			}
			writer.write("\n");
			writer.write("\n");
			
			// <cost of requirement i> <implementation risk of requirement i>
			int [] requirementsCosts = getIntVector(costUpperBound, numberOfRequirements);
			int [] requirementsRisks = getIntVector(riskUpperBound, numberOfRequirements);
			
			for (int i = 0; i < requirementsRisks.length; i++) {
				writer.write(requirementsCosts[i]+" "+requirementsRisks[i]+"\n");
			}
			writer.write("\n");
			
			//<importance of each requirement given by client i>
			int [][] importanceMatrix = getIntMatrix(importanceUpperBound, numberOfCustomers, numberOfRequirements);
			for (int i = 0; i < numberOfCustomers; i++) {
				for (int j = 0; j < numberOfRequirements; j++) {
					writer.write(importanceMatrix[i][j]+" ");
				}
				writer.write("\n");
			}
			writer.write("\n");
			
			//<budget for each release>
			int sumOfCosts = 0;
			for (int i = 0; i < numberOfRequirements; i++) {
				sumOfCosts += requirementsCosts[i];
			}
			
			for (int i = 0; i < numberOfReleases; i++) {
				writer.write((sumOfCosts/numberOfReleases)+" ");
			}
			
			writer.close();
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		

	}
	
	private int[] getIntVector(int upperLimit, int size) {
		int[] vector = new int[size];
		
		for (int i = 0; i < size; i++) {
			vector[i] = random.nextInt(upperLimit)+1;
		}

		return vector;
	}
	
	private int[][] getIntMatrix(int upperLimit, int m, int n) {
		int[][] matrix = new int[m][];
		for (int i = 0; i < m; i++) {
			matrix[i] = getIntVector(upperLimit, n);
		}
		
		return matrix;
	}
}