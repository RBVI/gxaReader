package edu.ucsf.rbvi.gxaReader.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import java.io.File;

import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.util.swing.IconManager;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperiment;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAExperimentTableModel;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;

public class GXAExperimentComponent extends JPanel implements CytoPanelComponent2 {
	final GXAManager gxaManager;
	final GXAExperiment gxaExperiment;
	final Font iconFont;


	// GXAExperimentTable gxaExperimentTable;
	Icon ico = null;


	public GXAExperimentComponent (final GXAManager gxaManager, 
	                               final GXAExperiment experiment) {
		this.gxaManager = gxaManager;
		this.gxaExperiment = experiment;

		IconManager iconManager = gxaManager.getService(IconManager.class);
    iconFont = iconManager.getIconFont(22.0f);
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

		JPanel buttonsPanelRight = new JPanel(new GridLayout(1, 3));


		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(buttonsPanelLeft, BorderLayout.WEST);
		topPanel.add(buttonsPanelRight, BorderLayout.EAST);
		this.add(topPanel, BorderLayout.NORTH);

		GXAExperimentTableModel tableModel = 
		 				new GXAExperimentTableModel(gxaManager, gxaExperiment);
		JTable gxaExperimentTable = new GXAExperimentTable(gxaManager, tableModel);

		JPanel mainPanel = new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(gxaExperimentTable);
		mainPanel.setLayout(new GridLayout(1, 1));
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		this.add(mainPanel, BorderLayout.CENTER);
	}
}
