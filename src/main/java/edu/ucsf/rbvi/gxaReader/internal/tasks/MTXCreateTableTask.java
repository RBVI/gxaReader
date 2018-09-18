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

import edu.ucsf.rbvi.gxaReader.internal.model.MatrixMarket;
import edu.ucsf.rbvi.gxaReader.internal.model.MTXManager;

public class MTXCreateTableTask extends AbstractTask {
	final MTXManager mtxManager;

	@Tunable(description="Matrix to create table from")
	public ListSingleSelection<String> matrix = null;

	@Tunable(description="Transpose", tooltip="From rows=genes to rows=cells")
	public boolean transpose = false;

	public MTXCreateTableTask(final MTXManager mtxManager) {
		this.mtxManager = mtxManager;
		List<String> matrices = new ArrayList<String>(mtxManager.getMatrixNames());
		if (matrices.size() > 0)
			matrix = new ListSingleSelection<String>(matrices);
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		if (matrix == null) return;
		MatrixMarket matrixMarket = mtxManager.getMatrix(matrix.getSelectedValue());
		if (matrixMarket == null) return;
		if (transpose) matrixMarket.setTranspose(transpose);
		CyTable table = matrixMarket.makeTable(matrix.getSelectedValue(), true);
	}
 
	@ProvidesTitle
	public String getTitle() {return "MTXCreateTable";}
}
