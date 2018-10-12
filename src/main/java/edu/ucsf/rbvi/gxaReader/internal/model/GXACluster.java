package edu.ucsf.rbvi.gxaReader.internal.model;

import java.io.InputStream;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import org.cytoscape.application.CyUserLog;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.utils.CSVReader;

public class GXACluster {
	public static String GXA_CLUSTER_URI = "https://www.ebi.ac.uk/gxa/sc/experiment/%s/download?fileType=cluster";

	final GXAManager gxaManager;
	final GXAExperiment experiment;

	final Logger logger;

	// The suggested k value
	int k = 0;

	// The number of K values provided
	int nK = 0;

	// The lowest K value
	int minK = 0;

	// The clusters
	int[][] clusters;

	// The headers
	String[] headers;

	// The Table model
	GXAClusterTableModel tableModel = null;

	public GXACluster(final GXAManager gxaManager, final GXAExperiment experiment) {
		this.gxaManager = gxaManager;
		this.experiment = experiment;

		logger = Logger.getLogger(CyUserLog.NAME);
	}

	public int[] getCluster() {
		if (k == 0)
			return getCluster(minK); // No default K was selected

		return getCluster(k);
	}

	public int[] getCluster(int kClust) {
		return clusters[kClust-minK];
	}

	public int getMinK() { return minK; }
	public int getK() { return k; }

	public static GXACluster fetchCluster(GXAManager gxaManager, String accession, 
	                                      GXAExperiment experiment, TaskMonitor monitor) {
		// Get the URI
		List<String[]> input = CSVReader.readCSVFromHTTP(monitor, GXA_CLUSTER_URI, accession);
		if (input == null || input.size() < 2) return null;

		int nclusters = input.size();
		int ncolumns = input.get(0).length-2;

		GXACluster gxaCluster = new GXACluster(gxaManager, experiment);

		gxaCluster.clusters = new int[ncolumns][nclusters];
		boolean first = true;
		for (String[] line: input) {
			if (first) {
				first = false;
				gxaCluster.headers = line;
				continue;
			}
			int thisK = Integer.parseInt(line[1]);
			if (line[0].equals("TRUE"))
				gxaCluster.k = thisK;
			if (gxaCluster.minK == 0)
				gxaCluster.minK = thisK;

			for (int i = 2; i < line.length; i++) {
				gxaCluster.clusters[i-2][thisK-gxaCluster.minK] = Integer.parseInt(line[i]);
			}

		}

		if (monitor != null) {
			monitor.showMessage(TaskMonitor.Level.INFO, "Read "+(nclusters-1)+" clusters.  Suggested K = "+gxaCluster.k);
			monitor.showMessage(TaskMonitor.Level.INFO, "   minimum K = "+gxaCluster.minK);
		} else {
			gxaCluster.logger.info("Read "+(nclusters-1)+" clusters.  Suggested K = "+gxaCluster.k);
			gxaCluster.logger.info("   minimum K = "+gxaCluster.minK);
		}

		return gxaCluster;
	}

	public GXAClusterTableModel getTableModel() {
		if (tableModel == null)
			tableModel = new GXAClusterTableModel(this);
		return tableModel;
	}

	public class GXAClusterTableModel extends AbstractTableModel {
		final GXACluster cluster;
		final GXAExperiment experiment;
		int nrows;
		int ncols;

		GXAClusterTableModel(final GXACluster cluster) {
			super();
			this.cluster = cluster;
			this.experiment = cluster.experiment;
			nrows = cluster.clusters[0].length-1;
			ncols = headers.length;
		}

		@Override
		public int getColumnCount() { return ncols; }

		@Override
		public String getColumnName(int column) {
			return headers[column];
		}

		@Override
		public int getRowCount() { 
			return nrows;
		}

		@Override
		public Class getColumnClass(int column) {
			switch(column) {
				case 0:
					return String.class;
				default:
					return Integer.class;
			}
		}

		@Override
		public Object getValueAt(int row, int column) {
			//System.out.println("getValueAt: "+row+","+column);
			switch (column) {
				case 0:
					return (row+cluster.minK) == k ? "True" : "False";
				case 1:
					return row+cluster.minK;
				default:
					int value = cluster.clusters[column][row];
					return new Integer(value);
			}
		}
	}
}
