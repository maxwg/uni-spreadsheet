package modernUIElements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class ModernJTextField extends JTextField {
	private static final long serialVersionUID = 4159783237856056109L;
	public static final char[] illegalCharacters = { '\n', '\r' };

	public ModernJTextField(int width, int height, final int charLim)
			throws FontFormatException, IOException {
		super();
		this.setBackground(new Color(250, 250, 255));
		this.setForeground(new Color(32, 32, 32));
		this.setSize(width, height);
		this.setPreferredSize(new Dimension(width, height));
		this.setMargin(new Insets(0, 3, 0, 3));
		this.setFont(Font
				.createFont(
						Font.TRUETYPE_FONT,
						ModernJTextField.class
								.getResourceAsStream("/res/fonts/OpenSans-Regular.ttf"))
				.deriveFont(15f));
		this.setBorder(BorderFactory.createEmptyBorder(1, 6, 0, 6));
		preventIllegalCharacters();
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (getText().length() > charLim)
					setText(getText().substring(0, charLim));
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void preventIllegalCharacters() {
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					for (char c : illegalCharacters) {
						if (c == arg0.getKeyChar())
							setText(getText().replace(c + "", ""));
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
	}
}
