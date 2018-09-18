package edu.ucsf.rbvi.gxaReader.internal.tasks;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.RequestsUIHelper;
import org.cytoscape.work.swing.TunableUIHelper;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAEntry;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;
import edu.ucsf.rbvi.gxaReader.internal.model.MatrixMarket;
import edu.ucsf.rbvi.gxaReader.internal.utils.HTTPUtils;

public class GXAShowResultsTask extends AbstractTask implements RequestsUIHelper {
	final GXAManager gxaManager;
	private TunableUIHelper uiHelper;
	private boolean refreshing;

	private ListSingleSelection<String> species = null;
	@Tunable(description="Species", gravity=1.0)
	public ListSingleSelection<String> getSpecies() {
		if (species == null) {
			species = new ListSingleSelection<String>(new ArrayList<>(gxaManager.getSpecies()));
		}
		return species;
	}

	public void setSpecies(ListSingleSelection<String> sp) {}

	private ListSingleSelection<GXAEntry> accession = null;
	@Tunable(description="Experiment accession", listenForChange="Species", gravity=2.0)
	public ListSingleSelection<GXAEntry> getAccession() {
		if (refreshing)
			return accession;

		String sp = species.getSelectedValue();
		if (species == null || sp == null)
			accession = new ListSingleSelection<GXAEntry>(gxaManager.getGXAEntries());
		else
			accession = new ListSingleSelection<GXAEntry>(gxaManager.getGXAEntries(sp));

		if (uiHelper != null) {
			refreshing = true; // Avoid infinite loops
			uiHelper.refresh(this);
			refreshing = false;
		}
		return accession;
	}

	public void setAccession(ListSingleSelection<GXAEntry> acc) {
	}

	public GXAShowResultsTask(final GXAManager gxaManager) {
		this.gxaManager = gxaManager;
		if (gxaManager.getSpecies().size() > 0)
			species = new ListSingleSelection<String>(new ArrayList<>(gxaManager.getSpecies()));
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		if (accession.getSelectedValue() == null) return;
		GXAEntry acc = accession.getSelectedValue();
		HTTPUtils.showResults(gxaManager, acc, taskMonitor);

	}

	@ProvidesTitle
	public String getTitle() {return "GXAShowResults";}

	public void setUIHelper(TunableUIHelper helper) {
		this.uiHelper = helper;
	}
}
