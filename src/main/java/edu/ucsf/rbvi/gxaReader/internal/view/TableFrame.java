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

import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;

public class TableFrame extends JFrame {
	final GXAManager gxaManager;
	final JTable table;
	// final Font iconFont;

	public TableFrame (final GXAManager gxaManager, final JTable table, String title) {
		super(title);

		this.gxaManager = gxaManager;
		this.table = table;

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		init();
	}

	public void changeTable(JTable table) {
	}

	private void init() {
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		this.pack();
		this.setVisible(true);
	}
}
