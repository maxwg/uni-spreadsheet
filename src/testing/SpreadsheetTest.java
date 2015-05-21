package testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import gui.Spreadsheet;

import java.awt.event.FocusEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.Test;

import data.CellIndex;

/**
 * 
 * SpreadsheetTest - This is a simple integration test. We basically set some
 * text within cells of the spread sheet and check they evaluate correctly.
 * 
 * @author Eric McCreath
 * 
 */

public class SpreadsheetTest {

	Spreadsheet gui;

	@Test
	public void testSimple() {
		gui = new Spreadsheet();
		try {

			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					selectAndSet(1, 3, "Some Text");
					selectAndSet(4, 1, "5.12");

				}
			});
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C2", gui.worksheet))
							.show(), "Some Text");
			assertEquals(
					gui.worksheet.lookup(new CellIndex("A5", gui.worksheet))
							.show(), "5.12");
		} catch (InvocationTargetException e) {
			fail();
		} catch (InterruptedException e) {
			fail();
		}
	}

	@Test
	public void testExpressionCal() {
		gui = new Spreadsheet();
		try {

			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					selectAndSet(2, 3, "Some Text");
					selectAndSet(3, 3, "23.4");
					selectAndSet(4, 3, "34.1");
					selectAndSet(5, 3, "2.6+C4*C5");

				}
			});

			assertEquals(
					gui.worksheet.lookup(new CellIndex("C3", gui.worksheet))
							.show(), "Some Text");
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C4", gui.worksheet))
							.show(), "23.4");
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C5", gui.worksheet))
							.show(), "34.1");
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C6", gui.worksheet))
							.show(), "800.54");

		} catch (InvocationTargetException e) {
			fail();
		} catch (InterruptedException e) {
			fail();
		}

	}

	@Test
	public void testFunctionCal() {
		gui = new Spreadsheet();
		try {

			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					selectAndSet(2, 3, "1.1");
					selectAndSet(3, 3, "2.2");
					selectAndSet(4, 3, "3.3");
					selectAndSet(5, 3, "SUM(C3,C4,C5)");
					selectAndSet(6, 3, "MAX(C3,C4,C5)");
					gui.functioneditor.updateWorksheet();

				}
			});
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C3", gui.worksheet))
							.show(), "1.1");
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C4", gui.worksheet))
							.show(), "2.2");
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C5", gui.worksheet))
							.show(), "3.3");
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C6", gui.worksheet))
							.show(), "6.6");
			assertEquals(
					gui.worksheet.lookup(new CellIndex("C7", gui.worksheet))
							.show(), "3.3");
		} catch (InvocationTargetException e) {
			fail();
		} catch (InterruptedException e) {
			fail();
		}
	}

	private void selectAndSet(int r, int c, String text) {
		gui.worksheetview.addRowSelectionInterval(r, r);
		gui.worksheetview.addColumnSelectionInterval(c, c);
		gui.cellEditTextField.setText(text);
		gui.worksheetview.getSelectedIndex().getCell().calcuate(gui.worksheet);
	}
}
