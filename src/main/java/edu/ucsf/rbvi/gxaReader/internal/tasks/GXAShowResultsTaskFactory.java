package edu.ucsf.rbvi.gxaReader.internal.tasks;

import java.io.InputStream;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;

public class GXAShowResultsTaskFactory extends AbstractTaskFactory {
	final GXAManager gxaManager;

	public GXAShowResultsTaskFactory(final GXAManager gxaManager) {
		super();
		this.gxaManager = gxaManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		TaskIterator ti = new TaskIterator();
		if (gxaManager.getGXAEntries().size() == 0) {
			ti.append(new GXAFetchEntriesTask(gxaManager));
		}
		ti.append(new GXAShowResultsTask(gxaManager));
		return ti;
	}

	@Override
	public boolean isReady() { return true; }

}
