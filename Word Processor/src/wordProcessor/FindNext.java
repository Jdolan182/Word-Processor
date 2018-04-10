package wordProcessor;

import java.awt.Cursor;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class FindNext extends JFrame {

	private JPanel contentPane;
	private JTextField txtSearch;

	private String searchText;
	private String text;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public FindNext() {
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("Find & Replace");
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 250, 450, 220);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblFindWhat = new JLabel("Find What:");
		lblFindWhat.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFindWhat.setBounds(12, 33, 66, 14);
		contentPane.add(lblFindWhat);

		txtSearch = new JTextField();
		txtSearch.setBounds(98, 30, 210, 30);
		contentPane.add(txtSearch);
		txtSearch.setColumns(10);

		JButton btnFindNext = new JButton("Find Next");
		btnFindNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				searchText = txtSearch.getText();
			}
		});
		btnFindNext.setBounds(335, 25, 98, 31);
		contentPane.add(btnFindNext);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnCancel.setBounds(335, 145, 98, 31);
		contentPane.add(btnCancel);
		
		JLabel lblCase = new JLabel("(Search is case sensitive)");
		lblCase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCase.setBounds(12, 115, 146, 14);
		contentPane.add(lblCase);
		
		JButton btnReplace = new JButton("Replace");
		btnReplace.setBounds(335, 65, 98, 31);
		contentPane.add(btnReplace);
		
		JButton btnReplaceAll = new JButton("Replace All");
		btnReplaceAll.setBounds(334, 105, 98, 31);
		contentPane.add(btnReplaceAll);
		
		JLabel lblReplaceWith = new JLabel("Replace With:");
		lblReplaceWith.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblReplaceWith.setBounds(12, 73, 76, 14);
		contentPane.add(lblReplaceWith);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(98, 70, 210, 30);
		contentPane.add(textField);
	}

	public void grabTextArea(JTextArea textArea) {
		text = textArea.toString();
	}
}
