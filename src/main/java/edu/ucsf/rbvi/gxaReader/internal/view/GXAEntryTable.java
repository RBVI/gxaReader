package edu.ucsf.rbvi.gxaReader.internal.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskMonitor;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAEntry;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAEntryTableModel;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.MatrixMarket;
import edu.ucsf.rbvi.gxaReader.internal.model.MTXManager;
import edu.ucsf.rbvi.gxaReader.internal.tasks.GXAFetchTask;
import edu.ucsf.rbvi.gxaReader.internal.tasks.MTXCreateTableTask;

public class GXAEntryTable extends JTable {
	final GXAManager gxaManager;
	List<GXAEntry> entries;

	static String[] columnNames = {"Loaded", "Experiment", "Assays", "Comparisons", "Organisms", "Experimental Variables"};

	static Color alternateColor = new Color(224,255,224);

	public GXAEntryTable (final GXAManager gxaManager, GXAEntryTableModel tableModel) {
		super(tableModel);
		this.gxaManager = gxaManager;
		entries = gxaManager.getGXAEntries();

		this.setAutoCreateRowSorter(true);
		this.setAutoCreateColumnsFromModel(true);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		// ((DefaultTableCellRenderer)this.getDefaultRenderer(String.class)).setFont(new Font("SansSerif", Font.BOLD, 10));

		JTableHeader header = getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(this));
		header.setFont(new Font("SansSerif", Font.BOLD, 10));

		// Figure out the row heights
		for (int i = 0; i < entries.size(); i++) {
			int lines = entries.get(i).getFactors().size();
			this.setRowHeight(i, lines*16);
		}

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table = (JTable) mouseEvent.getSource();
				Point point = mouseEvent.getPoint();
				int row = table.rowAtPoint(point);
				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
					// Load the experiment
					String acc = (String)table.getValueAt(row,0);
					TaskIterator tasks = new TaskIterator(new CreateTableTask(acc));
					gxaManager.executeTasks(tasks);
				}
			}
		});
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component returnComp = super.prepareRenderer(renderer, row, column);

		if (!returnComp.getBackground().equals(getSelectionBackground())) {
			Color bg = (row % 2 == 0 ? alternateColor : Color.WHITE);
			returnComp.setBackground(bg);
			returnComp.setFont(new Font("SansSerif", Font.PLAIN, 10));
			bg = null;
		}
		return returnComp;
	}

	public class CreateTableTask extends AbstractTask {
		String accession;

		CreateTableTask(String acc) {
			this.accession = acc;
		}

		@Override
		public void run(TaskMonitor taskMonitor) {
			GXAExperiment experiment = gxaManager.fetchExperiment(accession, taskMonitor);
			if (experiment != null)
				gxaManager.addExperiment(accession,experiment);

			// Register an Experiment Table
			CytoPanelComponent component = new GXAExperimentComponent(gxaManager, experiment);
			gxaManager.registerService(component, CytoPanelComponent.class, new Properties());
		}
	}

}
