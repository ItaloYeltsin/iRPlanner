package br.uece.goes.model;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.problems.ReleasePlanningProblem;
import jmetal.util.JMException;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PrinterSpreadsheet {

	public void printSolution(Problem problem, Solution solution, File file)
			throws JMException {

		ReleasePlanningProblem rpp = (ReleasePlanningProblem) problem;
		Solution sol = solution;

		// Create blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		// Create a blank sheet
		// XSSFSheet spreadsheet = workbook.createSheet(" Employee Info ");
		// Create row object
		XSSFRow row;

		// creating styles of cells
		XSSFCellStyle header_style = workbook.createCellStyle();
		XSSFCellStyle req_style = workbook.createCellStyle();
		XSSFCellStyle values_style = workbook.createCellStyle();

		header_style.setFillPattern(HSSFCellStyle.FINE_DOTS);
		req_style.setFillPattern(HSSFCellStyle.FINE_DOTS);
		values_style.setFillPattern(HSSFCellStyle.FINE_DOTS);
		
		XSSFColor colorHeader = new XSSFColor(new Color(111, 168, 220));
		XSSFColor colorReq = new XSSFColor(new Color(207, 226, 243));
		XSSFColor colorValues = new XSSFColor(new Color(11, 83, 148));

		XSSFFont whiteFont = workbook.createFont();
		whiteFont.setColor(HSSFColor.WHITE.index);

		header_style.setFillBackgroundColor(colorHeader);
		header_style.setFillForegroundColor(colorHeader);
		header_style.setAlignment(header_style.ALIGN_CENTER);

		req_style.setFillBackgroundColor(colorReq);
		req_style.setFillForegroundColor(colorReq);
		values_style.setFillBackgroundColor(colorValues);
		values_style.setFillForegroundColor(colorValues);
		values_style.setAlignment(values_style.ALIGN_CENTER);
		values_style.setFont(whiteFont);

		String[] reqDescriptions = rpp.getReqDescriptions();
		Variable[] arraySolution = solution.getDecisionVariables();
		List<Double> numberOfReleases = new ArrayList<>();

		try {
			numberOfReleases = getNumberOfReleases(arraySolution);
			System.out.println(arraySolution);
			Collections.sort(numberOfReleases, new Comparator() {
				public int compare(Object o1, Object o2) {
					double v1 = (double) o1;
					double v2 = (double) o2;
					return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
				}
			});
		} catch (JMException e1) {
			e1.printStackTrace();
		}

		System.out.println(numberOfReleases.size());

		XSSFSheet[] spreadsheets = new XSSFSheet[numberOfReleases.size() + 1];
		System.out.println(spreadsheets.length);
		for (int i = 0; i < spreadsheets.length-1; i++) {

			spreadsheets[i] = workbook.createSheet("Release "
					+ (i+1));
		}
		
		int reqspreadsheet = numberOfReleases.size();
		
		spreadsheets[reqspreadsheet] = workbook.createSheet("Requiments");

		for (Double release : numberOfReleases) {

			printHeaderRelease(spreadsheets[numberOfReleases.indexOf(release)],
					header_style);

			int rowid = 1;
			boolean costLine = true;
			for (int i = 0; i < arraySolution.length; i++) {

				if (arraySolution[i].getValue() == release) {

					// create spreadsheet for release
					row = spreadsheets[numberOfReleases.indexOf(release)]
							.createRow(rowid++);
					int cellid = 0;

					Cell cell_id = row.createCell(cellid++);
					cell_id.setCellValue(i);
					cell_id.setCellStyle(header_style);

					Cell cell_req = row.createCell(cellid++);
					cell_req.setCellValue(reqDescriptions[i]);
					cell_req.setCellStyle(req_style);

					if (costLine) {
						// cost of release ################################
						Cell cell_values = row.createCell(cellid++);
						cell_values.setCellValue(1000);
						cell_values.setCellStyle(values_style);

						// orcamento of release ################################
						cell_values = row.createCell(cellid++);
						cell_values.setCellValue(300);
						cell_values.setCellStyle(values_style);

						costLine = false;

						// print id
						// print reqDescription

						// if it is 1 line or 2 line written, print the cost and
						// budget
					}
				}

				spreadsheets[numberOfReleases.indexOf(release)]
						.autoSizeColumn(0);
				spreadsheets[numberOfReleases.indexOf(release)]
						.autoSizeColumn(1);
				spreadsheets[numberOfReleases.indexOf(release)]
						.autoSizeColumn(2);
				spreadsheets[numberOfReleases.indexOf(release)]
						.autoSizeColumn(3);

			}

			// Write the workbook in file system
			FileOutputStream out;
			try {
				out = new FileOutputStream(file);
				workbook.write(out);
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Writesheet.xlsx written successfully");
		}

		// spreadsheet de requisitos

		int[][] customerSatisfaction = rpp.getCustomerSatisfaction();
		int rowid = 0;

		row = spreadsheets[reqspreadsheet ].createRow(rowid);
		
		int numberCustomers = rpp.getCustomers();
		
		for (int i = 0; i < numberCustomers; i++) {
			
			Cell cell_c1_Imp = row.createCell(i+5);
			cell_c1_Imp.setCellValue(rpp.getCustomerImportance()[i]);
			cell_c1_Imp.setCellStyle(header_style);
		}
				



		printHeaderReq(spreadsheets[reqspreadsheet], values_style,numberCustomers);

		rowid = 2;
		for (int j = 0; j < reqDescriptions.length; j++) {

			row = spreadsheets[reqspreadsheet ].createRow(rowid++);
			int cellid = 0;

			// print id
			Cell cell_id = row.createCell(cellid++);
			cell_id.setCellValue(j);
			cell_id.setCellStyle(header_style);

			// print requiments
			Cell cell_req = row.createCell(cellid++);
			cell_req.setCellValue(reqDescriptions[j]);
			
			
			// cost
			Cell cell_cost = row.createCell(cellid++);
			cell_cost.setCellValue(rpp.getCost(j));
			cell_cost.setCellStyle(header_style);

			// print dependency
			Cell cell_dep = row.createCell(cellid++);
			int[][] precedencesMatrix = rpp.getPrecedence();
			List<Integer> precedences = precedencesOf(j, precedencesMatrix,
					reqDescriptions.length);

			String p = "";
			for (Integer precedence : precedences) {

				p += precedence + ";";
			}
			cell_dep.setCellValue(p);

			// print satisfaction
			Cell cell_sat = row.createCell(cellid++);
			cell_sat.setCellValue(rpp.getSatisfaction()[j]);
			cell_sat.setCellStyle(header_style);

			// print customers
			
			for (int k = 0; k < numberCustomers; k++) {
				
				Cell cell_c1 = row.createCell(cellid++);
				cell_c1.setCellValue(customerSatisfaction[k][j]);
			}
			


		}

		spreadsheets[reqspreadsheet ].autoSizeColumn(0);
		spreadsheets[reqspreadsheet ].autoSizeColumn(1);
		spreadsheets[reqspreadsheet ].autoSizeColumn(2);
		spreadsheets[reqspreadsheet ].autoSizeColumn(3);
		spreadsheets[reqspreadsheet ].autoSizeColumn(4);
		spreadsheets[reqspreadsheet ].autoSizeColumn(5);
		spreadsheets[reqspreadsheet ].autoSizeColumn(6);
		spreadsheets[reqspreadsheet ].autoSizeColumn(7);
	

		// Write the workbook in file system
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("written successfully");

	}

	private void printHeaderRelease(XSSFSheet spreadsheet,
			XSSFCellStyle header_style) {
		XSSFRow row;
		String[] titles = { "id", "selected Requirements", "Cost", "Budget" };

		// Iterate over data and write to sheet
		int rowid = 0;

		// header
		row = spreadsheet.createRow(rowid++);
		int cellid = 0;
		for (String title : titles) {
			Cell cell = row.createCell(cellid++);
			cell.setCellValue(title);
			cell.setCellStyle(header_style);
		}
	}

	private void printHeaderReq(XSSFSheet spreadsheet,
			XSSFCellStyle header_style, int numberCustomers) {
		XSSFRow row;
		String[] titles = new String[5+numberCustomers];
		
		titles[0] = "id";
		titles[1] = "requirements";
		titles[2] = "cost";
		titles[3] = "precedences";
		titles[4] = "satisfaction";
		
		
		for (int i = 0; i < numberCustomers; i++) {
			
			titles[i+5] = "customer_"+i+1;
		}

		// Iterate over data and write to sheet
		int rowid = 1;

		// header
		row = spreadsheet.createRow(rowid++);
		int cellid = 0;
		for (String title : titles) {
			Cell cell = row.createCell(cellid++);
			cell.setCellValue(title);
			cell.setCellStyle(header_style);
		}

	}

	private List<Integer> precedencesOf(int requirementId,
			int[][] precedencesMatrix, int numberOfRequirements) {

		List<Integer> precendences = new ArrayList<>();

		for (int i = 0; i < numberOfRequirements; ++i) {

			if (precedencesMatrix[requirementId][i] == 1)
				precendences.add(i);
		}

		return precendences;
	}

	private List<Double> getNumberOfReleases(Variable[] arraySolution)
			throws JMException {

		List<Double> releasesUsed = new ArrayList<>();

		for (int i = 0; i < arraySolution.length; i++) {

			if ((arraySolution[i].getValue() > 0.0)
					&& (!releasesUsed.contains(arraySolution[i].getValue()))) {

				releasesUsed.add(arraySolution[i].getValue());
			}
		}

		return releasesUsed;
	}
}
