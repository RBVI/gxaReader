package edu.ucsf.rbvi.gxaReader.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.service.util.CyServiceRegistrar;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;
import edu.ucsf.rbvi.gxaReader.internal.model.MatrixMarket;

public class GXAFetchDesignTask extends AbstractTask {
	final GXAManager gxaManager;

	@Tunable(description="Experiment to fetch design for")
	public ListSingleSelection<GXAExperiment> experiment = null;

	public GXAFetchDesignTask(final GXAManager gxaManager) {
		this.gxaManager = gxaManager;
		List<GXAExperiment> experiments = new ArrayList<GXAExperiment>(gxaManager.getExperiments());
		if (experiments.size() > 0)
			experiment = new ListSingleSelection<GXAExperiment>(experiments);
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		if (experiment == null) return;
		GXAExperiment exp = experiment.getSelectedValue();
		exp.fetchDesign(taskMonitor);

		// Add to the experiment
		// exp.addTable(table);
		// Add to our list of tables
		// gxaManager.addTable(exp, table);
	}
 
	@ProvidesTitle
	public String getTitle() {return "GXAFetchDesign";}
}
