package edu.ucsf.rbvi.gxaReader.internal.tasks;

import java.io.InputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.AbstractInputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.ucsf.rbvi.gxaReader.internal.model.MTXManager;

public class MTXReaderTaskFactory extends AbstractInputStreamTaskFactory implements TaskFactory {
	final CyFileFilter mtxFilter;
	final MTXManager mtxManager;

	public MTXReaderTaskFactory(final CyFileFilter mtxFilter, final MTXManager manager) {
		super(mtxFilter);
		this.mtxFilter = mtxFilter;
		this.mtxManager = manager;
	}

	@Override
	public TaskIterator createTaskIterator(InputStream is, String inputName) {
		TaskIterator ti = new TaskIterator(new MTXReaderTask(is, inputName, mtxManager));
		return ti;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new MTXImporterTask(mtxManager));
	}

	@Override
	public boolean isReady() { return true; }

}
