package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import modernUIElements.OJLabel;
import data.Cell;
import data.CellIndex;
import data.WorkSheet;

public class WorksheetView extends JTable implements TableModel {

	private static final int NUMROWS = 100;
	private static final int NUMCOL = 26;
	private static final int FIRSTCOLUMNWIDTH = 20;
	private static final int COLUMNWIDTH = 50;

	/**
	 * WorksheetView - This is the GUI view of a worksheet. It builds on and
	 * modifies the JTable Eric McCreath 2015
	 */
	private static final long serialVersionUID = 1L;

	WorkSheet worksheet;
	Spreadsheet spreadsheet;
	private CellIndex pCell = new CellIndex("A1", worksheet);
	private ArrayList<TableModelListener> listeners;
	private ArrayList<SelectionObserver> observers;

	public WorksheetView(WorkSheet worksheet, Spreadsheet ss) {
		this.worksheet = worksheet;
		this.spreadsheet = ss;
		listeners = new ArrayList<TableModelListener>();
		observers = new ArrayList<SelectionObserver>();
		this.setModel(this);
		this.setCellSelectionEnabled(true);
		this.setRowSelectionAllowed(false);
		this.setColumnSelectionAllowed(false);
		this.getColumnModel().getSelectionModel()
				.addListSelectionListener(this);
		(this.getSelectionModel()).addListSelectionListener(this);

		this.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int col = 0; col < NUMCOL + 1; col++) {
			this.getColumnModel()
					.getColumn(col)
					.setPreferredWidth(
							col == 0 ? FIRSTCOLUMNWIDTH : COLUMNWIDTH);
		}

		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setBackground(new Color(24, 24, 100));
		headerRenderer.setForeground(new Color(240, 240, 240));
		headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < getModel().getColumnCount(); i++) {
			getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
		}
		setBackground(new Color(251, 251, 255));
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (!(e.getKeyChar() == KeyEvent.VK_LEFT
						|| e.getKeyChar() == KeyEvent.VK_RIGHT
						|| e.getKeyChar() == KeyEvent.VK_DOWN
						|| e.getKeyChar() == KeyEvent.VK_UP
						|| e.getKeyChar() == KeyEvent.VK_TAB
						|| e.getKeyChar() == KeyEvent.CHAR_UNDEFINED || e
							.getKeyChar() == KeyEvent.VK_ENTER)) {
					spreadsheet.cellEditTextField.requestFocus();
					spreadsheet.cellEditTextField
							.setText(Character.isLetterOrDigit(e.getKeyChar())? e
									.getKeyChar() + "" : ""); // Only set text
																// if
																// alphanumeric
																// key,
																// else focus
				}
			}
		});
	}

	// getCellRenderer - provides the renderers for the cells. Note the first
	// column is just the index.
	public TableCellRenderer getCellRenderer(int row, int column) {
		if ((column == 0)) {
			return new TableCellRenderer() {

				@Override
				public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
					JLabel lab = new OJLabel("" + (row + 1), 12);
					lab.setOpaque(true);
					lab.setHorizontalAlignment(SwingConstants.CENTER);
					lab.setBackground(new Color(24, 24, 100));
					lab.setForeground(new Color(240, 240, 240));
					return lab;

				}

			};
		}
		return new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				JLabel lab = new OJLabel("" + ((Cell) value).show(), 12);
				if (isSelected
						|| hasFocus
						|| (table.getSelectedColumn() == column && table
								.getSelectedRow() == row)) {
					lab.setOpaque(true);
					lab.setBackground(new Color(220, 220, 250));
					pCell = new CellIndex(column-1, row, worksheet);
				}
				return lab;
			}

		};
	}

	@Override
	public int getColumnCount() {
		return NUMCOL + 1;
	}

	@Override
	public int getRowCount() {
		return NUMROWS;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return worksheet.lookup(new CellIndex(columnIndex - 1, rowIndex,
				worksheet));
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0)
			return "";
		return (char) ('A' + (char) (columnIndex - 1)) + "";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		informObservers();
	}

	public void addSelectionObserver(SelectionObserver o) {
		observers.add(o);
	}

	private void informObservers() {
		for (SelectionObserver o : observers)
			o.update();
	}

	public CellIndex getSelectedIndex() {
		return new CellIndex(this.getSelectedColumn() - 1,
				this.getSelectedRow(), worksheet);
	}
	
	public CellIndex getPreviousIndex(){
		return pCell;
	}
	
	public void setWorksheet(WorkSheet worksheet) {
		this.worksheet = worksheet;
		this.repaint();
	}

}
