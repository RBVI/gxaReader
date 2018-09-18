package edu.ucsf.rbvi.gxaReader.internal.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.model.MatrixMarket;
import edu.ucsf.rbvi.gxaReader.internal.model.MTXManager;

public class CSVReader {
	public static String delimiter = null;

	public static List<String[]> readCSV(TaskMonitor taskMonitor, String name) throws IOException, FileNotFoundException {
		return readCSV(taskMonitor, new File(name));
	}

	public static List<String[]> readCSV(TaskMonitor taskMonitor, File name) throws IOException, FileNotFoundException {
		taskMonitor.showMessage(TaskMonitor.Level.INFO, "Reading CSV file '"+name.toString()+"'");

		// Open the file
		BufferedReader input = new BufferedReader(new FileReader(name));
		return readCSV(taskMonitor, input);
	}

	public static List<String[]> readCSV(TaskMonitor taskMonitor, InputStream stream, String name) throws IOException {
		taskMonitor.showMessage(TaskMonitor.Level.INFO, "Reading CSV file '"+name.toString()+"'");
		BufferedReader input = new BufferedReader(new InputStreamReader(stream));
		return readCSV(taskMonitor, input);
	}

	public static List<String[]> readCSV(TaskMonitor taskMonitor, BufferedReader input) throws IOException, FileNotFoundException {

		// Read each row
		List<String[]> rowList = new ArrayList<>();
		do {
			String[] row = readRow(input);
			if (row == null)
				break;
			rowList.add(row);
		} while (true);

		taskMonitor.showMessage(TaskMonitor.Level.INFO, "Found "+rowList.size()+" rows with "+rowList.get(0).length+" labels each");
		return rowList;
	}

	private static String[] readRow(BufferedReader input) throws IOException {
		String row = input.readLine();
		// System.out.println("Row: "+row);
		if (row == null) return null;
		String[] columns;
		if (delimiter != null)
			columns = row.split(delimiter, -1);
		else {
			delimiter = "\t";
			columns = row.split(delimiter, -1);
			if (columns.length == 1) {
				delimiter = ",";
				columns = row.split(delimiter, -1);
				if (columns.length == 1) {
					delimiter = null;
					throw new RuntimeException("Only tabs and commas are supported column delimiters");
				}
			}
		}
		return columns;
	}
}
