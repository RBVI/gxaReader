package edu.ucsf.rbvi.gxaReader.internal.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.model.MatrixMarket;
import edu.ucsf.rbvi.gxaReader.internal.model.MTXManager;
import edu.ucsf.rbvi.gxaReader.internal.utils.CSVReader;

public class MTXImporter {
	public static String delimiter = null;
	final MTXManager mtxManager;

	public MTXImporter(final MTXManager mtxManager) {
		this.mtxManager = mtxManager;
	}

	public MatrixMarket readMTX(TaskMonitor taskMonitor, InputStream stream, String mtxInputName) {
		// We don't have row or column labels

		// Read in matrix
		MatrixMarket matrix = new MatrixMarket(mtxManager);
		try {
			matrix.readMTX(taskMonitor, stream, mtxInputName);
		} catch (Exception e) {
			taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Unable to read matrix market file from "+mtxInputName+": "+e.getMessage());
			return null;
		}

		return matrix;
	}

	public MatrixMarket readMTX(TaskMonitor taskMonitor, File mtxInputName, File rowInput, File colInput) {

		taskMonitor.showMessage(TaskMonitor.Level.INFO, "Reading matrix market (MTX) file '"+mtxInputName+"'");

		// CellRanger outputs three files: a genes file that contains gene ids (ensemble) and names,
		// a barcodes file with each barcode, and a matrix file with the actual counts in matrix market (MTX or MEX) format
		String mtxPath = mtxInputName.getAbsolutePath();

		// Read in row labels
		List<String[]> rowLabels;
		try {
			if (rowInput == null)
				rowLabels = CSVReader.readCSV(taskMonitor, mtxPath+"_rows");
			else
				rowLabels = CSVReader.readCSV(taskMonitor, rowInput);
		} catch (Exception e) {
			taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Unable to read row labels from "+rowInput+": "+e.getMessage());
			return null;
		}

		// Read in column labels
		List<String[]> colLabels;
		try {
			if (colInput == null)
				colLabels = CSVReader.readCSV(taskMonitor, mtxPath+"_cols");
			else
				colLabels = CSVReader.readCSV(taskMonitor, colInput);
		} catch (Exception e) {
			taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Unable to read column labels from "+colInput+": "+e.getMessage());
			return null;
		}

		// Read in matrix
		MatrixMarket matrix = new MatrixMarket(mtxManager, rowLabels, colLabels);
		try {
			matrix.readMTX(taskMonitor, mtxInputName);
		} catch (Exception e) {
			taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Unable to read matrix market file from "+mtxInputName+": "+e.getMessage());
			e.printStackTrace();
			return null;
		}

		if (matrix.getFormat() == MatrixMarket.MTXFORMAT.ARRAY) {
			taskMonitor.showMessage(TaskMonitor.Level.INFO, "Read dense mtx file with "+matrix.getNRows()+" rows and "+matrix.getNCols()+" columns");
		} else {
			taskMonitor.showMessage(TaskMonitor.Level.INFO, "Read sparse mtx file with "+matrix.getNRows()+" rows and "+matrix.getNCols()+" columns");
			taskMonitor.showMessage(TaskMonitor.Level.INFO, "     file  has "+matrix.getNonZeroCount()+" non-zero values"); 
		}

		double[][] data = matrix.getDoubleMatrix(Double.NaN);
		/*
		System.out.print("                 \t");
		for(int col = 0; col < matrix.getNCols(); col++) {
			System.out.print(colLabels.get(col)[1]+"\t");
		}
		DecimalFormat df2 = new DecimalFormat(".##");
		for (int row = 0; row < data.length; row++) {
			System.out.print("\n"+rowLabels.get(row)[0]+": "+rowLabels.get(row)[1]+"\t");
			for(int col = 0; col < data[0].length; col++) {
				double val = data[row][col];
				if (Double.isNaN(val))
					System.out.print("  -  \t");
				else
					System.out.print(df2.format(data[row][col])+"\t");
			}
			System.out.println();
		}
		*/

		mtxManager.addMatrix(mtxInputName.getName(), matrix);
		return matrix;
	}

}
