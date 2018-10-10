package edu.ucsf.rbvi.gxaReader.internal.view;

import java.awt.Component;

import java.io.File;

import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAEntryTableModel;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;

public class GXAEntryComponent implements CytoPanelComponent2 {
	final GXAManager gxaManager;
	JScrollPane scrollPane = null;
	GXAEntryTable gxaEntryTable;
	Icon ico = null;

	public GXAEntryComponent (final GXAManager gxaManager) {
		this.gxaManager = gxaManager;
	}

	@Override
	public String getIdentifier() {
		return "edu.ucsf.rbvi.gxaReader.GXAEntryComponent";
	}

	@Override
	public Component getComponent() {
		if (scrollPane == null) {
			if (gxaManager.getGXAEntries().size() == 0) {
				gxaManager.loadGXAEntries(null);
			}

			GXAEntryTableModel tableModel = new GXAEntryTableModel(gxaManager);
			gxaEntryTable = new GXAEntryTable(gxaManager, tableModel);
			scrollPane = new JScrollPane(gxaEntryTable);
		}

		return scrollPane;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	@Override
	public Icon getIcon() {
		if (ico == null) {
			try {
				ico = new ImageIcon(GXAEntryComponent.class.getResource("/images/ebi-embl.png"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ico;
	}

	@Override
	public String getTitle() {
		return "GXA Experiments";
	}
}
