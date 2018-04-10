package wordProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Preferences {

	private String currentFont;
	private String currentSize;
	private String currentLaF;

	private Properties prop;

	public void createConfig() {

		Properties prop = new Properties();
		OutputStream output = null;

		File f = new File("config.properties");
		if (!f.exists()) {

			try {

				output = new FileOutputStream("config.properties");

				prop.setProperty("LaF", "System Default");
				prop.setProperty("FontSize", "12");
				prop.setProperty("Font", "Arial");

				prop.store(output, null);

			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}

	public void changePreferences(String laF, String size, String font) {

		OutputStream output = null;

		try {

			prop.setProperty("LaF", laF);
			prop.setProperty("FontSize", size);
			prop.setProperty("Font", font);

			currentLaF = prop.getProperty("LaF").toString();
			currentSize = prop.getProperty("FontSize").toString();
			currentFont = prop.getProperty("Font").toString();
			
			output = new FileOutputStream("config.properties");

			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	public void readPreferences() {

		prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");
			prop.load(input);

			currentLaF = prop.getProperty("LaF").toString();
			currentSize = prop.getProperty("FontSize").toString();
			currentFont = prop.getProperty("Font").toString();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getLaF() {
		return currentLaF;
	}

	public int getSize() {
		int size = Integer.parseInt(currentSize);
		return size;
	}

	public String getFont() {
		return currentFont;
	}
}
