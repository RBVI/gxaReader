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

public class GXAFetchTask extends AbstractTask implements RequestsUIHelper {
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
		System.out.println("getAccession: species = "+sp);
		if (species == null || sp == null)
			accession = new ListSingleSelection<GXAEntry>(gxaManager.getGXAEntries());
		else
			accession = new ListSingleSelection<GXAEntry>(gxaManager.getGXAEntries(sp));

		if (uiHelper != null) {
			refreshing = true;
			uiHelper.refresh(this);
			refreshing = false;
		}
		return accession;
	}

	public void setAccession(ListSingleSelection<GXAEntry> acc) {
	}

	public GXAFetchTask(final GXAManager gxaManager) {
		this.gxaManager = gxaManager;
		System.out.println("Found "+gxaManager.getSpecies().size()+" species");
		if (gxaManager.getSpecies().size() > 0)
			species = new ListSingleSelection<String>(new ArrayList<>(gxaManager.getSpecies()));

		// accession = new ListSingleSelection<GXAEntry>(gxaManager.getGXAEntries());

	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		if (accession.getSelectedValue() == null) return;
		GXAExperiment experiment = gxaManager.fetchExperiment(accession.getSelectedValue(), taskMonitor);
		if (experiment != null)
			gxaManager.addExperiment(accession.getSelectedValue(),experiment);
	}
 
	@ProvidesTitle
	public String getTitle() {return "GXAFetch";}

	public void setUIHelper(TunableUIHelper helper) {
		System.out.println("setUIHelper called: helper = "+helper);
		this.uiHelper = helper;
	}
}
