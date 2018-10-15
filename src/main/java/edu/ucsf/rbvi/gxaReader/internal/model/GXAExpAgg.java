package edu.ucsf.rbvi.gxaReader.internal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import org.cytoscape.application.CyUserLog;
import org.cytoscape.service.util.CyServiceRegistrar;

// Experiment Aggregation
public class GXAExpAgg {
	final Logger logger;

	String accession = null;

	final GXAManager gxaManager;
	final GXAExperiment gxaExperiment;

	double mean[][];
	double variance[][];
	String headers[];

	public GXAExpAgg (final GXAManager manager, final GXAExperiment gxaExperiment) {
		this.gxaManager = manager;
		logger = Logger.getLogger(CyUserLog.NAME);
		this.gxaExperiment = gxaExperiment;
	}

	public void aggregateClusters(int k) {
		GXACluster gxaCluster = gxaExperiment.getClusters();
		MatrixMarket matrix = gxaExperiment.getMatrix();
		Map<Integer, List<String>> clusterList = gxaCluster.getClusterList(k);
		int ncolumns = k;
		int nrows = matrix.getNRows();
		mean = new double[ncolumns][nrows];
		variance = new double[ncolumns][nrows];
		headers = new String[ncolumns];
		for (int column = 0; column < k; column++) {

			// Calculate the mean
			Arrays.fill(mean[column], 0.0);
			headers[column] = "Cluster "+(column+1);
			System.out.println("Cluster "+(column+1));
			for (String assay: clusterList.get(column+1)) {
				System.out.println("Assay: "+assay);
				int matColumn = matrix.getColLabels().indexOf(assay);
				for (int row = 0; row < matrix.getNRows(); row++) {
					double value = matrix.getDoubleValue(row, matColumn+1);
					if (row == 0)
						System.out.println("value = "+value);
					if (!Double.isNaN(value))
						mean[column][row] += value/clusterList.get(column+1).size();
				}
			}

			// Now, calculate the variance
			Arrays.fill(variance[column], 0.0);
			for (String assay: clusterList.get(column+1)) {
				System.out.println("Assay: "+assay);
				int matColumn = matrix.getColLabels().indexOf(assay);
				for (int row = 0; row < matrix.getNRows(); row++) {
					double value = matrix.getDoubleValue(row, matColumn+1);
					double meanValue = mean[column][row];
					double varValue = (value-meanValue)*(value-meanValue)/(clusterList.get(column+1).size()-1);
					if (row == 0) {
						System.out.println("value = "+value);
						System.out.println("mean = "+meanValue);
						System.out.println("var = "+varValue);
						System.out.println("n = "+(clusterList.get(column+1).size()-1));
					}
					if (!Double.isNaN(value))
						variance[column][row] += varValue;
				}
			}
		}
	}

	public void aggregateFactors(String factor) {
		GXADesign gxaDesign = gxaExperiment.getDesign();
		MatrixMarket matrix = gxaExperiment.getMatrix();
		Map<String, List<String>> clusterList = gxaDesign.getClusterList(factor);
		int ncolumns = clusterList.size();
		int nrows = matrix.getNRows();
		mean = new double[ncolumns][nrows];
		variance = new double[ncolumns][nrows];
		headers = new String[ncolumns];

		int column = 0;
		for (String columnFactor: clusterList.keySet()) {
			Arrays.fill(mean[column], 0.0);
			headers[column] = columnFactor;
			for (String assay: clusterList.get(columnFactor)) {
				int matColumn = matrix.getColLabels().indexOf(assay);
				for (int row = 0; row < matrix.getNRows(); row++) {
					double value = matrix.getDoubleValue(row, matColumn);
					if (!Double.isNaN(value))
						mean[column][row] += value/clusterList.get(columnFactor).size();
				}
			}

			// Now, calculate the variance
			Arrays.fill(variance[column], 0.0);
			for (String assay: clusterList.get(column)) {
				int matColumn = matrix.getColLabels().indexOf(assay);
				for (int row = 0; row < matrix.getNRows(); row++) {
					double value = matrix.getDoubleValue(row, matColumn);
					if (!Double.isNaN(value))
						variance[column][row] += (value-mean[column][row])*(value-mean[column][row])/clusterList.get(columnFactor).size()-1;
				}
			}

			column++;
		}
	}

	public ExpAggTableModel getTableModel() {
		return new ExpAggTableModel();
	}

	public class ExpAggTableModel extends GXASubTableModel {
		public ExpAggTableModel() {
			super();
		}

		@Override
		public int getColumnCount() { return headers.length*2+1; }

		@Override
		public String getColumnName(int column) {
			if (column == 0) {
				return "Genes";
			}
			int hdrCol = (column-1)/2;
			System.out.println("hdrCol = "+hdrCol);
			String hdr = headers[hdrCol];
			if (column%2 == 1)
				hdr = hdr+" Avg";
			else
				hdr = hdr+" Var";
			return hdr;
		}

		@Override
		public int getRowCount() { 
			return gxaExperiment.getMatrix().getNRows(); 
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
					return gxaExperiment.getMatrix().getRowLabel(row);
				default:
					int col = (column-1)/2;
					double value;
					if (column%2 == 1)
						value = mean[col][row];
					else
						value = variance[col][row];
					if (Double.isNaN(value)) return null;
					return new Double(value);
			}
		}
	}
}
