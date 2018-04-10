package wordProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class FileManagement {

	String text;
	String name;
	int counter = 0;
	FileInputStream fis = null;
	FileOutputStream fout = null;
	StringBuilder sb = new StringBuilder(4096);

	public void loadFile(File fileName) {
		// , StandardCharsets.UTF_8 maybe add so that you have consistency from
		// one platform to the next.

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {

			char[] buffer = new char[8192]; // decent size buffer.
			sb.setLength(0);
			int len = 0;
			while ((len = reader.read(buffer)) >= 0) {
				sb.append(buffer, 0, len);

			}
		} catch (IOException ex) {
			// print error message in dialog box
			System.out.println("file couldn't be opened, or was incorrect or you clicked cancel");
		}
	}

	public StringBuilder showText() {

		return sb;
	}

	public void saveFile(String name, String text) {

		this.name = name;

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name)))) {

			writer.write(text);
			writer.flush();
			System.out.println("file saving worked");

		} catch (IOException e) {
			// print error message in dialog box
			e.printStackTrace();
			System.out.println("File failed to save or something went horribly wrong");
		}
	}

}
