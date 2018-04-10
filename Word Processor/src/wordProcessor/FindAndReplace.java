package wordProcessor;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class FindAndReplace {

	private int searchIndex = 0;
	private int length = 0;
	private int index = 0;
	private String searchWord = "";
	private boolean found = false;

	public boolean findWord(JTextArea textArea, String word) {

		String text = textArea.getText();
		searchWord = word;

		index = text.indexOf(searchWord, searchIndex);

		if (searchWord.equals("")) {
			JOptionPane.showMessageDialog(null, "You didn't input anything to search", "Find",
					JOptionPane.ERROR_MESSAGE);
			found = false;
			index = 0;

		} else if (!(text.contains(searchWord))) {
			JOptionPane.showMessageDialog(null, "Couldn't find: " + searchWord, "Find", JOptionPane.ERROR_MESSAGE);
			found = false;
			index = 0;
		} else {

			found = true;
		}

		return found;
	}

	public void selectWord(JTextArea textArea, String word) {

		searchWord = word;
		length = searchWord.length();
		String text = textArea.getText();

		textArea.select(index, (index + length));

		if (!((index + length) == (text.length()))) {
			searchIndex = (index + length);
		} else {
			searchIndex = 0;
		}

	}

	public void replace(JTextArea textArea, String word, String replaceWord) {

		textArea.select(index, (index + length));
		textArea.replaceSelection(replaceWord);

	}

	public void replaceAll(JTextArea textArea, String replaceWord) {
		textArea.setText(textArea.getText().replaceAll(searchWord, replaceWord));
	}

	public int getIndex() {
		return index;
	}

	public int getLength() {
		return index;
	}

	public String getSearch() {
		return searchWord;
	}

}
