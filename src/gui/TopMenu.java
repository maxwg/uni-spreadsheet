package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontFormatException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import modernUIElements.ModernButton;
import data.WorkSheet;

public class TopMenu extends JPanel {
	public static final int HEIGHT = 36;
	private static final long serialVersionUID = 2015353956108592989L;

	JFrame frame;
	public Spreadsheet ss;
	int sx, sy;
	int px, py;
	int fx, fy;
	JFileChooser filechooser = new JFileChooser();
	JButton exit;

	public TopMenu(JFrame window, Spreadsheet spreadsheet) throws FontFormatException, IOException {
		super(new BorderLayout());
		this.frame = window;
		this.ss = spreadsheet;
		this.setBackground(new Color(24,24,80));
		this.setPreferredSize(new Dimension(400,HEIGHT));
		addDragListeners();
		JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.add(new ModernButton("Exit", 60, HEIGHT, new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				System.exit(0);
				return null;
			}
		}), BorderLayout.LINE_END);
		this.add(buttonContainer, BorderLayout.LINE_START);
		buttonContainer.add(new ModernButton("Open", 60, HEIGHT, new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				int res = filechooser.showOpenDialog(frame);
				if (res == JFileChooser.APPROVE_OPTION) {
					ss.worksheet = WorkSheet.load(filechooser.getSelectedFile());
					ss.worksheetChange();
				}
				return null;
			}
		}));
		buttonContainer.add(new ModernButton("Save", 60, HEIGHT, new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				int res = filechooser.showOpenDialog(frame);
				if (res == JFileChooser.APPROVE_OPTION) {
					ss.worksheet.save(filechooser.getSelectedFile());
				}
				return null;
			}
		}));
		buttonContainer.add(new ModernButton("Clear", 60, HEIGHT, new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ss.worksheet = new WorkSheet();
				ss.worksheetChange();
				return null;
			}
		}));
		buttonContainer.add(new ModernButton("Define Functions", 120, HEIGHT, new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ss.functioneditor.setVisible(true);
				return null;
			}
		}));
	}
/*
 * @Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(EXITCOMMAND)) {
			exit();
		} else if (ae.getActionCommand().equals(SAVECOMMAND)) {
			int res = filechooser.showOpenDialog(jframe);
			if (res == JFileChooser.APPROVE_OPTION) {
				worksheet.save(filechooser.getSelectedFile());
			}
		} else if (ae.getActionCommand().equals(OPENCOMMAND)) {
			int res = filechooser.showOpenDialog(jframe);
			if (res == JFileChooser.APPROVE_OPTION) {
				worksheet = WorkSheet.load(filechooser.getSelectedFile());
				worksheetChange();
			}
		} else if (ae.getActionCommand().equals(CLEARCOMMAND)) {
			worksheet = new WorkSheet();
			worksheetChange();
		} else if (ae.getActionCommand().equals(EDITFUNCTIONCOMMAND)) {
			functioneditor.setVisible(true);
		} else if (ae.getActionCommand().equals(CALCULATECOMMAND)){
			worksheetview.valueChanged(null);
			worksheet.calculate();
		}
	}
 */

	private void addDragListeners() {
		this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				sx = e.getXOnScreen();
				sy = e.getYOnScreen();
				fx = frame.getX();
				fy = frame.getY();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				int dx = arg0.getXOnScreen() - sx;
				int dy = arg0.getYOnScreen()-sy;
				frame.setLocation(fx+dx, fy+dy);
			}
		});
	}
}