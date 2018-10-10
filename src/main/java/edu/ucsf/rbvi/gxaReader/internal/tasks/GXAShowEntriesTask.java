package edu.ucsf.rbvi.gxaReader.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;

public class GXAShowEntriesTask extends AbstractTask {
	final GXAManager gxaManager;

	public GXAShowEntriesTask(final GXAManager gxaManager) {
		this.gxaManager = gxaManager;
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		taskMonitor.setTitle("Show GXA Entries Table");
		taskMonitor.setStatusMessage("Showing Entries Table");
		gxaManager.showEntriesTable(true);
	}

	@ProvidesTitle
	public String getTitle() {return "Show GXA Entries";}
}
