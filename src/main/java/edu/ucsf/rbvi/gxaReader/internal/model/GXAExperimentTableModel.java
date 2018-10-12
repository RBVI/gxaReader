package edu.ucsf.rbvi.gxaReader.internal.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAEntry;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;
import edu.ucsf.rbvi.gxaReader.internal.model.MatrixMarket;

public class GXAExperimentTableModel extends AbstractTableModel {
	final GXAManager gxaManager;
	final GXAExperiment gxaExperiment;
	final MatrixMarket matrixMarket;

	public GXAExperimentTableModel (final GXAManager gxaManager, 
	                                final GXAExperiment experiment) {
		super();
		this.gxaManager = gxaManager;
		this.gxaExperiment = experiment;
		this.matrixMarket = experiment.getMatrix();
	}

	@Override
	public int getColumnCount() { return matrixMarket.getNCols(); }

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return matrixMarket.isTransposed() ? "Barcodes" : "Genes";
		}
		return matrixMarket.getColumnLabel(column-1);
	}

	@Override
	public int getRowCount() { 
		return matrixMarket.getNRows(); 
	}

	@Override
	public Class getColumnClass(int column) {
		switch (column) {
			case 0:
				return String.class;
			default:
				return Double.class;
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		//System.out.println("getValueAt: "+row+","+column);
		switch (column) {
			case 0:
				return matrixMarket.getRowLabel(row);
			default:
				double v = matrixMarket.getDoubleValue(row, column);
				if (Double.isNaN(v)) return null;
				return new Double(v);
		}
	}

}
