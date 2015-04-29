package modernUIElements;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ModernScrollPane extends JScrollPane {

	private static final long serialVersionUID = 989801111061500658L;
	public ModernScrollPane(Component c, final Color trackShade, final Color thumbShade) {
		super(c, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setBorder(BorderFactory.createEmptyBorder());
		final JScrollBar fancyVScrollBar = new JScrollBar(JScrollBar.VERTICAL);
		fancyVScrollBar.setBorder(BorderFactory.createEmptyBorder());
		fancyVScrollBar.setPreferredSize(new Dimension(10, this.getHeight()));
		fancyVScrollBar.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		final JScrollBar fancyHScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 60, 0, 10000);
		fancyHScrollBar.setUnitIncrement(10);
		fancyHScrollBar.setBorder(BorderFactory.createEmptyBorder());
		fancyHScrollBar.setPreferredSize(new Dimension(this.getWidth(), 10));
		fancyHScrollBar.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		fancyVScrollBar.setUI(new BasicScrollBarUI(){
			@Override
			protected void installComponents() {
				switch (scrollbar.getOrientation()) {
				case 		JScrollBar.VERTICAL:
					incrButton = createIncreaseButton(SOUTH);
					decrButton = createDecreaseButton(NORTH);
					break;

				case JScrollBar.HORIZONTAL:
					if (scrollbar.getComponentOrientation().isLeftToRight()) {
						incrButton = createIncreaseButton(EAST);
						decrButton = createDecreaseButton(WEST);
					} else {
						incrButton = createIncreaseButton(WEST);
						decrButton = createDecreaseButton(EAST);
					}
					break;
				}
				thumbColor = thumbShade;
				thumbDarkShadowColor = new Color((int)(thumbShade.getRed()/1.75), (int)(thumbShade.getGreen()/1.75),(int)(thumbShade.getBlue()/1.75));
				thumbHighlightColor = new Color((int)(thumbShade.getRed()/0.875), (int)(thumbShade.getGreen()/0.875), (int)(thumbShade.getBlue()/0.875));
				thumbLightShadowColor = new Color((int)(thumbShade.getRed()/0.875), (int)(thumbShade.getGreen()/0.875), (int)(thumbShade.getBlue()/0.875));
				incrGap = -16;
				decrGap = -16;
				trackColor = trackShade;
				// Force the children's enabled state to be updated.
				scrollbar.setEnabled(scrollbar.isEnabled());
				setBackground(trackShade);
			}
		});
		fancyHScrollBar.setUI(new BasicScrollBarUI(){
			@Override
			protected void installComponents() {
				switch (scrollbar.getOrientation()) {
				case 		JScrollBar.VERTICAL:
					incrButton = createIncreaseButton(SOUTH);
					decrButton = createDecreaseButton(NORTH);
					break;

				case JScrollBar.HORIZONTAL:
					if (scrollbar.getComponentOrientation().isLeftToRight()) {
						incrButton = createIncreaseButton(EAST);
						decrButton = createDecreaseButton(WEST);
					} else {
						incrButton = createIncreaseButton(WEST);
						decrButton = createDecreaseButton(EAST);
					}
					break;
				}
				thumbColor = thumbShade;
				thumbDarkShadowColor = new Color((int)(thumbShade.getRed()/1.75), (int)(thumbShade.getGreen()/1.75),(int)(thumbShade.getBlue()/1.75));
				thumbHighlightColor = new Color((int)(thumbShade.getRed()/0.875), (int)(thumbShade.getGreen()/0.875), (int)(thumbShade.getBlue()/0.875));
				thumbLightShadowColor = new Color((int)(thumbShade.getRed()/0.875), (int)(thumbShade.getGreen()/0.875), (int)(thumbShade.getBlue()/0.875));
				incrGap = -16;
				decrGap = -16;
				trackColor = trackShade;
				// Force the children's enabled state to be updated.
				scrollbar.setEnabled(scrollbar.isEnabled());
				setBackground(trackShade);
			}
		});
		this.setVerticalScrollBar(fancyVScrollBar);
		this.setHorizontalScrollBar(fancyHScrollBar);
	}

}
