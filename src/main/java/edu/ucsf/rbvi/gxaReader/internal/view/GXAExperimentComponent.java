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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableModel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.util.swing.IconManager;

import edu.ucsf.rbvi.gxaReader.internal.model.GXACluster;
import edu.ucsf.rbvi.gxaReader.internal.model.GXADesign;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperimentTableModel;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;
import edu.ucsf.rbvi.gxaReader.internal.model.GXASubTableModel;

public class GXAExperimentComponent extends JPanel implements CytoPanelComponent2 {
	final GXAManager gxaManager;
	final GXAExperiment gxaExperiment;
	final Font iconFont;
	final GXAExperimentComponent thisComponent;

	// GXAExperimentTable gxaExperimentTable;
	Icon ico = null;


	public GXAExperimentComponent (final GXAManager gxaManager, 
	                               final GXAExperiment experiment) {
		this.gxaManager = gxaManager;
		this.gxaExperiment = experiment;

		IconManager iconManager = gxaManager.getService(IconManager.class);
    iconFont = iconManager.getIconFont(22.0f);
		this.setLayout(new BorderLayout());
		thisComponent = this;	// Access to inner classes
		init();
	}

	@Override
	public String getIdentifier() {
		return "edu.ucsf.rbvi.gxaReader.GXAExperimentComponent";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	@Override
	public Icon getIcon() {
		if (ico == null) {
			try {
				ico = new ImageIcon(GXAExperimentComponent.class.getResource("/images/ebi-embl.png"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ico;
	}

	@Override
	public String getTitle() {
		return gxaExperiment.getAccession();
	}

	private void init() {
		JPanel buttonsPanelLeft = new JPanel(new GridLayout(1, 3));

		{
			JButton showClusters = new JButton("Show clusters");
			showClusters.setFont(new Font("SansSerif", Font.PLAIN, 10));
      showClusters.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String title = "Clusters for "+gxaExperiment.getAccession();
					GXACluster clusters = gxaExperiment.getClusters();
					showTable(title, clusters.getTableModel(), gxaExperiment);
				}
			});
			buttonsPanelLeft.add(showClusters);
		}

		{
			JButton showDesign = new JButton("Show design");
			showDesign.setFont(new Font("SansSerif", Font.PLAIN, 10));
      showDesign.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String title = "Experimental design for "+gxaExperiment.getAccession();
					GXADesign design = gxaExperiment.getDesign();
					showTable(title, design.getTableModel(), gxaExperiment);
				}
			});
			buttonsPanelLeft.add(showDesign);
		}

		JPanel buttonsPanelRight = new JPanel(new GridLayout(1, 3));

		{
			JButton delete= new JButton(IconManager.ICON_TRASH_O);
      delete.setFont(iconFont);
      delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gxaManager.unregisterService(thisComponent, CytoPanelComponent.class);
				}
			});
      delete.setToolTipText("Remove experiment");
      delete.setBorderPainted(false);
      delete.setContentAreaFilled(false);
      delete.setFocusPainted(false);
      delete.setBorder(BorderFactory.createEmptyBorder(2,2,2,10));
			buttonsPanelRight.add(delete);

		}
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(buttonsPanelLeft, BorderLayout.WEST);
		topPanel.add(buttonsPanelRight, BorderLayout.EAST);
		this.add(topPanel, BorderLayout.NORTH);

		GXAExperimentTableModel tableModel = 
		 				new GXAExperimentTableModel(gxaManager, gxaExperiment);
		JTable gxaExperimentTable = new GXAExperimentTable(gxaManager, tableModel);

		JScrollPane scrollPane = new JScrollPane(gxaExperimentTable);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
	}

	public void showTable(String title, GXASubTableModel model, GXAExperiment experiment) {
		JTable table = new SimpleTable(gxaManager, model);
		new TableFrame (gxaManager, table, title);
	}

}
