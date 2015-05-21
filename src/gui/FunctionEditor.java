package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import modernUIElements.ModernButton;
import modernUIElements.ModernScrollPane;

import data.WorkSheet;
import expressions.Expression;
import expressions.ParseException;
import expressions.functions.Function;

public class FunctionEditor extends JDialog implements ActionListener {
	/**
	 * FunctionEditor - a simple modal dialog to edit the text of the functions.
	 * 
	 * @author Eric McCreath
	 * 
	 */

	WorkSheet worksheet;
	public JTextArea textarea;

	private static String defaultFn1 = "max(n values){\n	i = 0;\n	max = -2000000;\n	while(i < #values){\n		if(values[i] > max){\n			max = values[i];\n		}\n		i=i+1;\n	}\n	return max;\n}\n\n";
	private static String defaultFn2 = "sum(n values){\n	i = 0;\n	sum = 0;\n	while(i < #values){\n		sum = sum + values[i];\n		i=i+1;\n	}\n	return sum;\n}\n\n";
	private static String defaultFn3 = "useless(3 nums){\n	return nums[0] - nums[1] * sin(nums[2]);\n}\n\n";
	private static String defaultFn4 = "fact(1 num){\n i = 1;\n	fact = 1;\n	while(i <= num[0]){\n		fact = fact * i;\n		i=i+1;\n	}\n	return fact;\n}\n\n";
	private static String defaultFn5 = "recurse(2 num){\n	return fact(num[0])/fact(num[1]);\n}";
	private static String defaultFn6 = "inverseFact(1 num){\n	return 1/fact(num[0]);\n}\n\n";
	
	public FunctionEditor(WorkSheet worksheet) throws FontFormatException,
			IOException {
		setBackground(new Color(36, 36, 36));
		this.worksheet = worksheet;
		textarea = new JTextArea(20, 50);
		textarea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		textarea.setBackground(new Color(36, 36, 36));
		textarea.setForeground(new Color(242, 242, 255));
		textarea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		textarea.setTabSize(2);
		textarea.setCaretColor(Color.WHITE);
		textarea.setText(defaultFn1+defaultFn2+defaultFn3+defaultFn6+defaultFn4+defaultFn5);
		JButton closebutton = new ModernButton("Save", true);
		closebutton.addActionListener(this);
		this.setModal(true);
		this.getContentPane().add(
				new ModernScrollPane(textarea, new Color(42, 42, 42),
						new Color(24, 24, 24)), BorderLayout.CENTER);
		this.getContentPane().add(closebutton, BorderLayout.PAGE_END);
		this.pack();
	}

	public void setWorksheet(WorkSheet ws) {
		worksheet = ws;
		textarea.setText(worksheet.getFuctions());
	}

	public void actionPerformed(ActionEvent ae) { 
		if (save()) {
			updateWorksheet();
			dispose();
		}
	}

	public boolean save() {
		try {
			Expression.funs = Function.parseFunctions(textarea.getText(),
					worksheet);
			Expression.refreshFuns();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Invalid functions! Please modify and resave.");
			return false;
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this,
					"Invalid functions! Please modify and resave.\n\n Error:"
							+ e.getMessage());
			return false;
		}
	}

	public void updateWorksheet() {
		worksheet.setFunctions(textarea.getText());
	}
}
