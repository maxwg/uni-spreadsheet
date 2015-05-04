package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import modernUIElements.ModernScrollPane;
import modernUIElements.OJLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import data.Cell;
import data.CellIndex;
import data.WorkSheet;

public class Spreadsheet implements Runnable, SelectionObserver,
		DocumentListener {

	private static final Dimension PREFEREDDIM = new Dimension(630, 400);
	/**
	 * Spreadsheet - a simple spreadsheet program. Eric McCreath 2015
	 */

	JFrame jframe;
	public WorksheetView worksheetview;
	public FunctionEditor functioneditor;
	public WorkSheet worksheet;
	public JTextArea cellEditTextField;
	JPanel toolarea;
	JLabel selectedCellLabel;
	JLabel equationDisplay = new JLabel();
	ModernScrollPane eqScroll;
	BufferedImage equationImage;
	Graphics2D eqGraphics;
	Thread renderThread;
	TeXIcon icon;
	TeXFormula formula;

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
			jframe.setBackground(new Color(255, 255, 255));
			jframe.setUndecorated(true);
			renderThread = new Thread() {
			};

			worksheet = new WorkSheet();
			worksheetview = new WorksheetView(worksheet, this);
			worksheetview.addSelectionObserver(this);

			// set up the tool area
			toolarea = new JPanel(new BorderLayout());
			toolarea.setBackground(new Color(255,255,255));
			toolarea.add(new TopMenu(jframe, this), BorderLayout.PAGE_START);

			selectedCellLabel = new OJLabel("--", 12);
			selectedCellLabel.setForeground(new Color(240, 240, 240));
			selectedCellLabel.setOpaque(true);
			selectedCellLabel.setBackground(new Color(24, 24, 24));
			selectedCellLabel.setHorizontalAlignment(SwingConstants.CENTER);
			selectedCellLabel.setBorder(BorderFactory.createEmptyBorder(0, 0,
					0, 2));
			selectedCellLabel.setPreferredSize(new Dimension(32,
					selectedCellLabel.getPreferredSize().height));
			toolarea.add(selectedCellLabel, BorderLayout.LINE_START);
			cellEditTextField = new JTextArea(1, 20);
			cellEditTextField.setMargin(new Insets(5, 5, 5, 5));
			cellEditTextField
					.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			cellEditTextField.getDocument().addDocumentListener(this);
			cellEditTextField.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
					renderMaths();
				}

				@Override
				public void keyPressed(KeyEvent e) {
					int caretPos = cellEditTextField.getCaret().getDot();
					int pos = -1;
					ArrayList<Integer> linepos = new ArrayList<Integer>();
					while ((pos = cellEditTextField.getText().indexOf('\n',
							pos + 1)) != -1)
						linepos.add(pos);
					if (linepos.size() == 0) {
						linepos.add(cellEditTextField.getText().length());
						linepos.add(0);
					}
					if (e.getKeyCode() == KeyEvent.VK_DOWN
							&& caretPos > linepos.get(linepos.size() - 1)
							|| e.getKeyCode() == KeyEvent.VK_UP
							&& caretPos <= linepos.get(0)
							|| e.getKeyCode() == KeyEvent.VK_LEFT
							&& caretPos == 0
							|| e.getKeyCode() == KeyEvent.VK_RIGHT
							&& caretPos >= cellEditTextField.getText().length()) {
						worksheetview.requestFocus();
					}
				}
			});
			cellEditTextField.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
					for(CellIndex i : worksheetview.getPreviousIndex().getCell().referencedBy)
						i.getCell().calcuate(worksheet);
					for(CellIndex i : worksheetview.getPreviousIndex().referencedCells)
						i.getCell().referencedBy.remove(worksheetview.getPreviousIndex());
					worksheetview.getPreviousIndex().getCell()
							.calcuate(worksheet);
					try {
						worksheetview.getPreviousIndex().referencedCells = worksheetview.getPreviousIndex().getCell().getReferences();
					} catch (IllegalArgumentException | IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					for(CellIndex i : worksheetview.getPreviousIndex().referencedCells)
						i.getCell().referencedBy.add(worksheetview.getPreviousIndex());
					update();
				}

				@Override
				public void focusGained(FocusEvent e) {
				}
			});
			ModernScrollPane cetfs = new ModernScrollPane(cellEditTextField,
					new Color(80, 80, 80), new Color(24, 24, 24));
			toolarea.add(cetfs, BorderLayout.CENTER);
			equationDisplay.setForeground(new Color(0, 0, 0));
			eqScroll = new ModernScrollPane(equationDisplay, new Color(80, 80,
					80), new Color(24, 24, 24));

			toolarea.add(eqScroll, BorderLayout.LINE_END);
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

	public void renderMaths() {
		renderThread.interrupt();
		renderThread = (new Thread() {
			@Override
			public void run() {
				try {
					String latex;
					if (!Thread.currentThread().isInterrupted())
						latex = worksheetview.getSelectedIndex().getCell()
								.getLatex(worksheet);
					else
						return;
					latex = latex == null ? worksheetview.getSelectedIndex()
							.getCell().text() : latex;
					formula = new TeXFormula(latex);
					if (!Thread.currentThread().isInterrupted())
						icon = formula.new TeXIconBuilder()
								.setStyle(TeXConstants.STYLE_DISPLAY)
								.setSize(13).build();
					else
						return;
					icon.setInsets(new Insets(3, 3, 3, 3));

					if (!Thread.currentThread().isInterrupted())
						equationImage = new BufferedImage(icon.getIconWidth(),
								icon.getIconHeight() < 22 ? 22 : icon
										.getIconHeight(),
								BufferedImage.TYPE_INT_ARGB);
					else
						return;
					if (!Thread.currentThread().isInterrupted())
						eqGraphics = equationImage.createGraphics();
					else
						return;
					eqGraphics.setColor(Color.white);
					eqGraphics.fillRect(0, 0, icon.getIconWidth(), icon
							.getIconHeight() < 22 ? 22 : icon.getIconHeight());
					if (!Thread.currentThread().isInterrupted())
						icon.paintIcon(equationDisplay, eqGraphics, 0, 0);
					else
						return;
					equationDisplay.setIcon(new ImageIcon(equationImage));
					equationDisplay.repaint();
					equationDisplay.setPreferredSize(new Dimension(icon
							.getIconWidth(), icon.getIconHeight()));

					Dimension maxBound = new Dimension(
							(icon.getIconWidth() > 250 ? 250
									: icon.getIconWidth())
									+ (icon.getIconHeight() > 120 ? 10 : 0),
							(icon.getIconHeight() > 120 ? 120 : icon
									.getIconHeight())
									+ (icon.getIconWidth() > 250 ? 10 : 0));
					eqScroll.setSize(maxBound);
					eqScroll.setPreferredSize(maxBound);
					toolarea.updateUI();
				} catch (Exception e) {
					equationDisplay.setIcon(null);
					equationDisplay.repaint();
					eqScroll.setSize(new Dimension(0, 0));
					eqScroll.setPreferredSize(new Dimension(0, 0));
					toolarea.updateUI();
				}
			}
		});
		renderThread.start();

	}

	void worksheetChange() {
		worksheetview.setWorksheet(worksheet);
		functioneditor.setWorksheet(worksheet);
		worksheetview.repaint();
	}

	@Override
	public void update() {
		CellIndex index = worksheetview.getSelectedIndex();
		selectedCellLabel.setText(index.show());
		cellEditTextField.setText(worksheet.lookup(index).text());
		renderMaths();
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
