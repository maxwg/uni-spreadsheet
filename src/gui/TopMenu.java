package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TopMenu extends JPanel {
	public static final int HEIGHT = 36;
	private static final long serialVersionUID = 2015353956108592989L;

	JFrame frame;
	Spreadsheet ss;
	int sx, sy;
	JButton exit;

	public TopMenu(JFrame window, Spreadsheet ss) throws FontFormatException, IOException {
		super(null);
		this.frame = window;
		this.ss = ss;
		this.setBackground(new Color(42, 42, 42));
		this.setPreferredSize(new Dimension(getPreferredSize().width,HEIGHT));
		addDragListeners();
	}

	public void manageResize() {
		exit.setLocation(this.getWidth()-exit.getWidth(), 0);
	}

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
				sx = e.getX();
				sy = e.getY();
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
				frame.setLocation(arg0.getX() - sx + frame.getX(), arg0.getY()
						- sy + frame.getY());
			}
		});
	}
}