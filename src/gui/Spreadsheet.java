package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import modernUIElements.ModernButton;
import modernUIElements.ModernJTextField;
import modernUIElements.ModernScrollPane;
import modernUIElements.OJLabel;
import data.Cell;
import data.CellIndex;
import data.WorkSheet;

public class Spreadsheet implements Runnable, ActionListener,
		SelectionObserver, DocumentListener {

	private static final Dimension PREFEREDDIM = new Dimension(630, 400);
	/**
	 * Spreadsheet - a simple spreadsheet program. Eric McCreath 2015
	 */
	private static final String CALCULATECOMMAND = "calculatecommand";

	JFrame jframe;
	public WorksheetView worksheetview;
	public FunctionEditor functioneditor;
	public WorkSheet worksheet;
	public JButton calculateButton;
	public JTextField cellEditTextField;
	JLabel selectedCellLabel;

	public Spreadsheet() {
		SwingUtilities.invokeLater(this);
	}

	public static void main(String[] args) {
		new Spreadsheet();
	}

	public void run() {
		try {
			jframe = new JFrame("Spreadsheet");
			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jframe.setBackground(new Color(24, 24, 24));
			// set up the menu bar
			/*
			 * JMenuBar bar = new JMenuBar(); JMenu menu = new JMenu("File");
			 * bar.add(menu); makeMenuItem(menu, "New", CLEARCOMMAND);
			 * makeMenuItem(menu, "Open", OPENCOMMAND); makeMenuItem(menu,
			 * "Save", SAVECOMMAND); makeMenuItem(menu, "Exit", EXITCOMMAND);
			 * menu = new JMenu("Edit"); bar.add(menu); makeMenuItem(menu,
			 * "EditFunction", EDITFUNCTIONCOMMAND);
			 * 
			 * jframe.setJMenuBar(bar);
			 */
			jframe.setUndecorated(true);

			worksheet = new WorkSheet();
			worksheetview = new WorksheetView(worksheet, this);
			worksheetview.addSelectionObserver(this);

			// set up the tool area
			JPanel toolarea = new JPanel(new BorderLayout());
			toolarea.setBackground(new Color(24, 24, 24));
			toolarea.add(new TopMenu(jframe, this), BorderLayout.PAGE_START);

			calculateButton = new ModernButton("Calculate", 120, 24, null);
			calculateButton.addActionListener(this);
			calculateButton.setActionCommand(CALCULATECOMMAND);

			selectedCellLabel = new OJLabel("--", 12);
			selectedCellLabel.setForeground(new Color(240, 240, 240));
			selectedCellLabel.setHorizontalAlignment(SwingConstants.CENTER);
			selectedCellLabel.setBorder(BorderFactory.createEmptyBorder(0, 0,
					0, 2));
			selectedCellLabel.setPreferredSize(new Dimension(32,
					selectedCellLabel.getPreferredSize().height));
			toolarea.add(selectedCellLabel, BorderLayout.LINE_START);
			cellEditTextField = new ModernJTextField(200, 28, 1000);
			cellEditTextField.getDocument().addDocumentListener(this);
			toolarea.add(cellEditTextField, BorderLayout.CENTER);
			toolarea.add(calculateButton, BorderLayout.LINE_END);

			functioneditor = new FunctionEditor(worksheet);

			jframe.getContentPane().add(
					new ModernScrollPane(worksheetview, new Color(24, 24, 100),
							new Color(50, 50, 150)), BorderLayout.CENTER);
			jframe.getContentPane().add(toolarea, BorderLayout.PAGE_START);

			jframe.setVisible(true);
			jframe.setPreferredSize(PREFEREDDIM);
			jframe.pack();
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(CALCULATECOMMAND)) {
			worksheetview.valueChanged(null);
			worksheet.calculate();
		}
	}

	void worksheetChange() {
		worksheetview.setWorksheet(worksheet);
		functioneditor.setWorksheet(worksheet);
		worksheetview.repaint();
	}

	private void exit() {
		System.exit(0);
	}

	@Override
	public void update() {
		CellIndex index = worksheetview.getSelectedIndex();
		selectedCellLabel.setText(index.show());
		cellEditTextField.setText(worksheet.lookup(index).text());
		jframe.repaint();
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		textChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		textChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		textChanged();
	}

	private void textChanged() {
		CellIndex index = worksheetview.getSelectedIndex();
		Cell current = worksheet.lookup(index);
		current.setText(cellEditTextField.getText());
		worksheetview.repaint();
	}
}
