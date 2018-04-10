package wordProcessor;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Statistics {

	private int words;
	private int letters;
	private int lines;

	public void calculateStatistics(JTextArea textArea) {

		words = 0;
		letters = 0;
		lines = 0;

		String[] linesArray = textArea.getText().split("\n");
		this.lines = linesArray.length;
		for (int i = 0; i < linesArray.length; i++) {
			String[] words = linesArray[i].split(" ");
			this.words = (this.words + words.length);
			for (int j = 0; j < words.length; j++) {
				this.letters = (this.letters + words[j].length());
			}
		}
		//quick fix for wrong display when empty textArea
		if(words == 1 & letters == 0){
			words = 0;
			letters = 0;
			lines = 0;
		}
	}

	public void displayStatistics() {		
		JOptionPane.showMessageDialog(null, statisticsString(), "Information",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	private String statisticsString() {
		return "Lines:          " + lines + "\nWords:        " + words + "\nCharacter:  " + letters;
	}
}
