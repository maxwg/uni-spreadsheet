package modernUIElements;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

public class ModernButton extends JButton {
	private static final long serialVersionUID = 4889596406034462097L;
	public static final Color NORMAL = new Color(24, 24, 24);
	public static final Color OVER = new Color(42, 42, 42);
	public static final Color PRESS = new Color(80, 80, 80);
	private static Font REGULAR;
	private static Font LIGHT;
	static {
		try {
			REGULAR = Font.createFont(Font.TRUETYPE_FONT, ModernButton.class
					.getResourceAsStream("/res/fonts/OpenSans-Regular.ttf"));
			LIGHT = Font.createFont(Font.TRUETYPE_FONT, ModernButton.class
					.getResourceAsStream("/res/fonts/OpenSans-Light.ttf"));
		} catch (Exception e) {
			System.err.println("Missing vital resources!");
			System.exit(-1);
		}
	}

	private Callable<?> func;
	public ModernButton(String text, boolean useLightFont)
			throws FontFormatException, IOException {
		super(text);
		if (useLightFont)
			initialize(LIGHT);
		else
			initialize(REGULAR);
	}
	
	public ModernButton(String text, int w, int h, boolean useLightFont)
			throws FontFormatException, IOException {
		super(text);
		this.setSize(w, h);
		this.setPreferredSize(new Dimension(w, h));
		if (useLightFont)
			initialize(LIGHT);
		else
			initialize(REGULAR);
	}

	public ModernButton(String text, int w, int h, Callable<?> func,
			boolean useLightFont) throws FontFormatException, IOException {
		super(text);
		this.func = func;
		this.setSize(w, h);
		this.setPreferredSize(new Dimension(w, h));
		if (useLightFont)
			initialize(LIGHT);
		else
			initialize(REGULAR);
	}

	public ModernButton(String text, int w, int h, Callable<?> func)
			throws FontFormatException, IOException {
		this(text, w, h, func, false);
	}

	public ModernButton(Icon icon, int w, int h, Callable<?> func)
			throws FontFormatException, IOException {
		super(icon);
		this.func = func;
		this.setSize(w, h);
		this.setPreferredSize(new Dimension(w, h));
		initialize(REGULAR);
	}

	void initialize(Font f) throws FontFormatException, IOException {
		this.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		this.setBackground(NORMAL);
		this.setForeground(new Color(220, 220, 220));
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.setFont((REGULAR).deriveFont(13f));
		this.setUI(new BasicButtonUI() {
			public void installUI(JComponent c) {
				super.installUI(c);

				c.addMouseListener(new MouseListener() {

					@Override
					public void mouseReleased(MouseEvent e) {
						JComponent c = (JComponent) e.getComponent();
						c.setBackground(OVER);
					}

					@Override
					public void mousePressed(MouseEvent e) {
						JComponent c = (JComponent) e.getComponent();
						c.setBackground(PRESS);
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						JComponent c = (JComponent) e.getComponent();
						c.setBackground(OVER);
						c.repaint();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						JComponent c = (JComponent) e.getComponent();
						c.setBackground(NORMAL);
						c.repaint();
					}

					@Override
					public void mouseClicked(MouseEvent arg0) {
						try {
							if (func != null)
								func.call();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

		});
	}
}
