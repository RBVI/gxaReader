package edu.ucsf.rbvi.gxaReader.internal.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAEntry;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;

public class GXAEntryTableModel extends AbstractTableModel {
	final GXAManager gxaManager;
	List<GXAEntry> entries = null;

	static String[] columnNames = {"Accession", "Loaded", "Experiment", "Assays", "Comparisons", "Organisms", "Experimental Variables"};

	public GXAEntryTableModel (final GXAManager gxaManager) {
		super();
		this.gxaManager = gxaManager;
		entries = gxaManager.getGXAEntries();
	}

	@Override
	public int getColumnCount() { return columnNames.length; }

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getRowCount() { 
		return entries.size(); 
	}

	@Override
	public Class getColumnClass(int column) {
		switch (column) {
			case 0:
				return String.class;
			case 1:
				return String.class;
			case 2:
				return String.class;
			case 3:
				return Integer.class;
			case 4:
				return Integer.class;
			case 5:
				return String.class;
			case 6:
				return String.class;
		}
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int column) {
		GXAEntry entry = entries.get(row);
		switch (column) {
			case 0:
				return entry.getAccession();
			case 1:
				return entry.getDate();
			case 2:
				return entry.getDescription();
			case 3:
				return new Integer(entry.getAssays());
			case 4:
				return new Integer(entry.getContrasts());
			case 5:
				return entry.getSpecies();
			case 6:
				return nlList(entry.getFactors());
		}
		return null;
	}

	private String nlList(List<String> list) {
		String ret = null;
		for (String l: list) {
			if (ret == null)
				ret = "<html>"+l;
			else
				ret += "<br/>"+l;
		}
		return ret+"</html>";
	}
}
