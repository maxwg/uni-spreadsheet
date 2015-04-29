package data;
/**
 * Cell - an object of this class holds the data of a single cell. 
 * 
 * @author Eric McCreath
 */

public class Cell {

	private String text; // this is the text the person typed into the cell
	private Double calculatedValue; // this is the current calculated value for
									// the cell

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
		try {
			calculatedValue = Double.parseDouble(text);
		} catch (NumberFormatException nfe) {
			calculatedValue = null;
		}
	}

	public String show() { // this is what is viewed in each Cell
		return calculatedValue == null ? text : calculatedValue.toString();
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

	public boolean isEmpty() {
		return text.equals("");
	}
}
