package edu.ucsf.rbvi.gxaReader.internal.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.utils.CSVReader;

public class GXAExperiment {
	public static String GXA_MTX_URI = "https://www.ebi.ac.uk/gxa/sc/experiment/%s/download/zip?fileType=quantification-filtered";

	List<String[]> rowTable = null;
	List<String[]> colTable = null;
	MatrixMarket mtx = null;
	final GXAManager gxaManager;
	final MTXManager mtxManager;

	public GXAExperiment (GXAManager manager) {
		this.gxaManager = manager;
		this.mtxManager = manager.getMTXManager();
	}

	public void fetchMTX (String accession, TaskMonitor monitor) {
		// Get the URI
		String fetchString = String.format(GXA_MTX_URI, accession);

		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(fetchString);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			if (response1.getStatusLine().getStatusCode() != 200) {
				return;
			}
			HttpEntity entity1 = response1.getEntity();

			try {
				ZipInputStream zipStream = new ZipInputStream(entity1.getContent());

				ZipEntry entry;
				while ((entry = zipStream.getNextEntry()) != null) {
					String name = entry.getName();
					System.out.println("Name = "+name);
					if (name.endsWith(".mtx_cols")) {
						colTable = CSVReader.readCSV(monitor, zipStream, name);
						if (mtx != null) 
							mtx.setColumnTable(colTable);
					} else if (name.endsWith(".mtx_rows")) {
						rowTable = CSVReader.readCSV(monitor, zipStream, name);
						if (mtx != null) 
							mtx.setRowTable(rowTable);
					} else if (name.endsWith(".mtx")) {
						mtx = new MatrixMarket(mtxManager, rowTable, colTable);
						mtx.readMTX(monitor, zipStream, name);
					}
					zipStream.closeEntry();
				}
				zipStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				response1.close();
			}
		} catch (Exception e) {}
		gxaManager.addExperiment(accession, this);
		mtxManager.addMatrix(mtx.toString(), mtx);
	}

	public ZipInputStream getZipStream(String uri, TaskMonitor monitor) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(uri);
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		if (response1.getStatusLine().getStatusCode() != 200) {
			return null;
		}
		ZipInputStream stream = null;
		try {
			HttpEntity entity1 = response1.getEntity();
			stream = new ZipInputStream(entity1.getContent());
		} finally {
			response1.close();
		}
		return stream;
	}
}
