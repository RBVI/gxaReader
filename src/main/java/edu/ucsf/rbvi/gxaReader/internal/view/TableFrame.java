package edu.ucsf.rbvi.gxaReader.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import org.cytoscape.util.swing.IconManager;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAExpAgg;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;
import edu.ucsf.rbvi.gxaReader.internal.model.GXASubTableModel;

public class TableFrame extends JFrame {
	
	public enum FrameType {
		CLUSTER, DESIGN, AGGREGATION
	}

	final GXAManager gxaManager;
	final GXAExperiment gxaExperiment;
	final SimpleTable table;
	FrameType frameType;
	// final Font iconFont;
	JButton aggregate;

	public TableFrame (final GXAManager gxaManager, final GXAExperiment experiment, 
	                   final SimpleTable table, String title, FrameType frameType) {
		super(title);

		this.gxaManager = gxaManager;
		this.gxaExperiment = experiment;
		this.frameType = frameType;
		this.table = table;
		table.setTableFrame(this);

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		init();
	}

	public void changeTable(JTable table) {
	}

	public void enableAggregation() { aggregate.setEnabled(true); }

	private void init() {
		if (frameType != FrameType.AGGREGATION) {
			JPanel buttons = new JPanel();
			aggregate = new JButton("Aggregate Experiment");
			aggregate.setEnabled(false);
			aggregate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GXAExpAgg agg = new GXAExpAgg(gxaManager, gxaExperiment);
					String title;
					if (frameType.equals(FrameType.CLUSTER)) {
						int sortedK = gxaExperiment.getClusters().getSortedK();
						agg.aggregateClusters(sortedK);
						title = "Expression Aggregated by K="+sortedK;
					} else if (frameType.equals(FrameType.DESIGN)) {
						String sortedRow = gxaExperiment.getDesign().getSortedRow();
						agg.aggregateFactors(sortedRow);
						title = "Expression Aggregated by "+sortedRow;
					} else {
						return;
					}
					GXASubTableModel aggTableModel = agg.getTableModel();
					showTable(title, aggTableModel, gxaExperiment, FrameType.AGGREGATION);
				}
			});
			buttons.add(aggregate);
			this.getContentPane().add(buttons, BorderLayout.NORTH);
		}

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		this.pack();
		this.setVisible(true);
	}

	public void showTable(String title, GXASubTableModel model, 
	                      GXAExperiment experiment, FrameType frameType) {
		SimpleTable table = new SimpleTable(gxaManager, model);
		new TableFrame (gxaManager, experiment, table, title, frameType);
	}
}
