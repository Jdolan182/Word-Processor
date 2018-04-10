package wordProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class History {

	private int historyMax = 5;
	private int fileNum = 5;

	private String[] files = { "", "", "", "", "" };

	Properties prop = new Properties();

	public void createHistory() {

		Properties prop = new Properties();
		OutputStream output = null;

		File f = new File("history.properties");
		if (!f.exists()) {

			try {

				output = new FileOutputStream("history.properties");

				prop.setProperty("file1", "");
				prop.setProperty("file2", "");
				prop.setProperty("file3", "");
				prop.setProperty("file4", "");
				prop.setProperty("file5", "");

				// save properties to project root folder
				prop.store(output, null);

			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}

	public void addFileToHistory(String fileName) {

		OutputStream output = null;

		readHistory();
		addFile();

		try {

			prop.setProperty("file" + fileNum, fileName);

			output = new FileOutputStream("history.properties");

			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	public void readHistory() {

		prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("history.properties");

			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void addFile() {

		fileNum = 0;
		int changeTest = fileNum;

		for (int i = 5; i > 0; i--) {
			if (prop.getProperty("file" + i).isEmpty()) {
				fileNum = i;
				break;
			}
		}
		if (changeTest == fileNum) {
			for (int j = 5; j > 1; j--) {

				prop.setProperty("file" + (j), (prop.getProperty("file" + (j - 1))));
			}
			fileNum = 1;

		}
	}

	public String[] getFiles() {
		for (int i = 1; i <= historyMax; i++) {
			files[i - 1] = prop.getProperty("file" + i).toString();
		}
		return files;
	}
}
