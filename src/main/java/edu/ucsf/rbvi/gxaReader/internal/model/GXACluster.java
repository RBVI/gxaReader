package edu.ucsf.rbvi.gxaReader.internal.model;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.utils.CSVReader;

public class GXACluster {
	public static String GXA_CLUSTER_URI = "https://www.ebi.ac.uk/gxa/sc/experiment/%s/download?fileType=cluster";

	final GXAManager gxaManager;

	// The suggested k value
	int k = 0;

	// The number of K values provided
	int nK = 0;

	// The lowest K value
	int minK = 0;

	// The clusters
	int[][] clusters;

	public GXACluster(GXAManager gxaManager) {
		this.gxaManager = gxaManager;
	}

	public int[] getCluster() {
		if (k == 0)
			return getCluster(minK); // No default K was selected

		return getCluster(k);
	}

	public int[] getCluster(int kClust) {
		return clusters[kClust-minK];
	}

	public static GXACluster fetchCluster(GXAManager gxaManager, String accession, TaskMonitor monitor) {
		// Get the URI
		List<String[]> input = CSVReader.readCSVFromHTTP(monitor, GXA_CLUSTER_URI, accession);
		if (input == null || input.size() < 2) return null;

		int nclusters = input.size();
		int ncolumns = input.get(0).length-2;

		GXACluster gxaCluster = new GXACluster(gxaManager);

		gxaCluster.clusters = new int[ncolumns][nclusters];
		boolean first = true;
		for (String[] line: input) {
			if (first) {
				first = false;
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

		monitor.showMessage(TaskMonitor.Level.INFO, "Read "+(nclusters-1)+" clusters.  Suggested K = "+gxaCluster.k);
		monitor.showMessage(TaskMonitor.Level.INFO, "   minimum K = "+gxaCluster.minK);

		return gxaCluster;
	}
}
