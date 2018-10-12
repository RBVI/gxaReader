package edu.ucsf.rbvi.gxaReader.internal.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;

public class SimpleTable extends JTable {
	final GXAManager gxaManager;
	int specialRow;

	static Color alternateColor = new Color(234,255,234);

	public SimpleTable (final GXAManager gxaManager, TableModel tableModel) {
		super(tableModel);
		this.gxaManager = gxaManager;
		this.specialRow = specialRow;

		this.setAutoCreateRowSorter(true);
		this.setAutoCreateColumnsFromModel(true);
		// this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int nColumns = tableModel.getColumnCount();
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(100);
		for (int col = 1; col < nColumns; col++) {
			columnModel.getColumn(col).setPreferredWidth(100);
		}

		JTableHeader header = getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(this));
		header.setFont(new Font("SansSerif", Font.BOLD, 10));

		doLayout();
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
}
