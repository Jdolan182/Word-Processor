package wordProcessor;

import java.awt.GraphicsEnvironment;

public class Formatting {

	public String[] getAllFonts() {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = (e.getAvailableFontFamilyNames());
		return fonts;
	}
}
