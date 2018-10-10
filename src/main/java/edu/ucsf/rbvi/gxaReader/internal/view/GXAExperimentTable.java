package edu.ucsf.rbvi.gxaReader.internal.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperimentTableModel;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;

public class GXAExperimentTable extends JTable {
	final GXAManager gxaManager;

	static Color alternateColor = new Color(224,255,224);

	public GXAExperimentTable (final GXAManager gxaManager, GXAExperimentTableModel tableModel) {
		super(tableModel);
		this.gxaManager = gxaManager;

		this.setAutoCreateRowSorter(true);
		this.setAutoCreateColumnsFromModel(true);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component returnComp = super.prepareRenderer(renderer, row, column);

		if (!returnComp.getBackground().equals(getSelectionBackground())) {
			Color bg = (row % 2 == 0 ? alternateColor : Color.WHITE);
			returnComp.setBackground(bg);
			bg = null;
		}
		return returnComp;
	}
}
