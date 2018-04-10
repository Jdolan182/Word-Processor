package wordProcessor;

import java.awt.print.PrinterException;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Print {
		
	
	public void print(JTextArea textArea){
		try {
			boolean complete = textArea.print();
			if (complete == true) {
				JOptionPane.showMessageDialog(null, "Done Printing", "Information", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "Printing!", "Printer", JOptionPane.ERROR_MESSAGE);
			}
		} catch (PrinterException pe) {
			JOptionPane.showMessageDialog(null, pe);
		}
	}
	
}
