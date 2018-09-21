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

public class GXACreateTableTask extends AbstractTask {
	final GXAManager gxaManager;

	@Tunable(description="Experiment to create table from")
	public ListSingleSelection<GXAExperiment> experiment = null;

	@Tunable(description="Transpose", tooltip="From rows=genes to rows=cells")
	public boolean transpose = false;

	public GXACreateTableTask(final GXAManager gxaManager) {
		this.gxaManager = gxaManager;
		List<GXAExperiment> experiments = new ArrayList<GXAExperiment>(gxaManager.getExperiments());
		if (experiments.size() > 0)
			experiment = new ListSingleSelection<GXAExperiment>(experiments);
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		if (experiment == null) return;
		GXAExperiment exp = experiment.getSelectedValue();
		MatrixMarket matrixMarket = exp.getMatrix();
		if (matrixMarket == null) return;
		if (transpose) matrixMarket.setTranspose(transpose);
		CyTable table = matrixMarket.makeTable(exp.getAccession(), true);

		// Add to the experiment
		// exp.addTable(table);
		// Add to our list of tables
		// gxaManager.addTable(exp, table);
	}
 
	@ProvidesTitle
	public String getTitle() {return "GXACreateTable";}
}
