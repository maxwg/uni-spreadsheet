package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import modernUIElements.ModernButton;
import modernUIElements.ModernScrollPane;

import data.WorkSheet;

public class FunctionEditor extends JDialog implements ActionListener {
/**
 * FunctionEditor - a simple modal dialog to edit the text of the functions. 
 * 
 * @author Eric McCreath
 * 
 */
	
	
	WorkSheet worksheet;
	public JTextArea textarea;

	public FunctionEditor(WorkSheet worksheet) throws FontFormatException, IOException {
		setBackground(new Color(36,36,36));
		this.worksheet = worksheet;
		textarea = new JTextArea(20, 50);
		textarea.setFont(new Font(Font.MONOSPACED,Font.PLAIN, 12));
		textarea.setBackground(new Color(36,36,36));
		textarea.setForeground(new Color(242,242,255));
		textarea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JButton closebutton = new ModernButton("Close", true);
		closebutton.addActionListener(this);
		this.setModal(true);
		this.getContentPane().add(new ModernScrollPane(textarea,new Color(42,42,42),new Color(24,24,24)), BorderLayout.CENTER);
		this.getContentPane().add(closebutton, BorderLayout.PAGE_END);
        this.pack();
	}

	public void setWorksheet(WorkSheet ws) {
		worksheet = ws;
		textarea.setText(worksheet.getFuctions());
	}
	public void actionPerformed(ActionEvent ae) { // it is always the close button
		updateWorksheet();
		dispose();
	}
	public void updateWorksheet() {
		worksheet.setFunctions(textarea.getText());
	}
}
