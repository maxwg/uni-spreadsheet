package data;

import java.util.HashSet;
import java.util.List;

import expressions.Expression;

/**
 * Cell - an object of this class holds the data of a single cell.
 * 
 * @author Eric McCreath
 */

public class Cell {

	private String text; // this is the text the person typed into the cell
	private Double calculatedValue; // this is the current calculated value for
									// the cell
	private Expression calculatedExpression;
	public HashSet<CellIndex> referencedBy = new HashSet<CellIndex>();
	
	public Cell(String text) {
		this.text = text;
		calculatedValue = null;
	}

	public Cell() {
		text = "";
		calculatedValue = null;
	}

	public Double value() {
		return calculatedValue;
	}

	public void calcuate(WorkSheet worksheet) {
		if (!text.equals("")) {
			try {
				calculatedExpression = Expression.calculate(text, worksheet);
				calculatedValue = calculatedExpression == null ? null
						: calculatedExpression.evaluate();
			} catch (Exception e) {
				calculatedValue = null;
				e.printStackTrace();
			}
		} else
			calculatedValue = null;
	}

	public String show() { // this is what is viewed in each Cell
		return calculatedValue == null ? text : calculatedValue.toString();
	}

	public String getLatex(WorkSheet worksheet) {
		calcuate(worksheet);
		return calculatedExpression == null ? null : calculatedExpression
				.toLatex();
	}

	public String text() {
		return text;
	}

	@Override
	public String toString() {
		return text + "," + calculatedValue;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public List<CellIndex> getReferences(){
		return calculatedExpression.getReferencedCells();
	}

	public boolean isEmpty() {
		return text.equals("");
	}
}
