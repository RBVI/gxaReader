package edu.ucsf.rbvi.gxaReader.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAEntry;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;
import edu.ucsf.rbvi.gxaReader.internal.model.MatrixMarket;

public class GXAFetchEntriesTask extends AbstractTask {
	final GXAManager gxaManager;

	public GXAFetchEntriesTask(final GXAManager gxaManager) {
		this.gxaManager = gxaManager;
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		taskMonitor.setTitle(getTitle());
		taskMonitor.setStatusMessage("Fetching all GXA entries");
		gxaManager.loadGXAEntries(taskMonitor);
	}

	@ProvidesTitle
	public String getTitle() {return "GXA Fetch Entries";}
}
