package edu.ucsf.rbvi.gxaReader.internal.model;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.utils.CSVReader;

public class GXADesign {
	public static String GXA_DESIGN_URI = "https://www.ebi.ac.uk/gxa/sc/experiment/%s/download?fileType=experiment-design";

	final GXAManager gxaManager;

	// The clusters
	String[] columns;
	Map<String, String[]> rows;

	public GXADesign(GXAManager gxaManager) {
		this.gxaManager = gxaManager;
	}

	public static GXADesign fetchDesign(GXAManager gxaManager, String accession, TaskMonitor monitor) {
		// Get the URI
		List<String[]> input = CSVReader.readCSVFromHTTP(monitor, GXA_DESIGN_URI, accession);
		if (input == null || input.size() < 2) return null;

		int nrows = input.size();
		int ncolumns = input.get(0).length;

		GXADesign gxaDesign = new GXADesign(gxaManager);

		gxaDesign.columns = input.get(0);
		gxaDesign.rows = new HashMap<>();

		boolean first = true;
		for (String[] line: input) {
			if (first) {
				first = false;
			} else
				gxaDesign.rows.put(line[0], line);
		}

		return gxaDesign;
	}
}
