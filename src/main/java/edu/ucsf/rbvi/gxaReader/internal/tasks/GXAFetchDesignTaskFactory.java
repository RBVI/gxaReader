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

public class GXAFetchDesignTaskFactory extends AbstractTaskFactory {
	final GXAManager gxaManager;

	public GXAFetchDesignTaskFactory(final GXAManager gxaManager) {
		super();
		this.gxaManager = gxaManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		TaskIterator ti = new TaskIterator(new GXAFetchDesignTask(gxaManager));
		return ti;
	}

	@Override
	public boolean isReady() { return true; }

}
