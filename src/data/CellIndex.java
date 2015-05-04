package data;

import java.util.ArrayList;
import java.util.List;

import expressions.Expression;

/**
 * 
 * CellIndex -
 * 
 * @author Eric McCreath
 */

public class CellIndex extends Expression {
	int row, column; // these values stored in row and column start at 0.
						// Although when
	// they are displayed the column starts with 'A' and the row starts with
	// '1'.
	// e.g. cell index A1 will be row 0 and column 0.
	
	WorkSheet worksheet;
	public List<CellIndex> referencedCells= new ArrayList<CellIndex>();
	
	public CellIndex(int column, int row, WorkSheet worksheet) {
		this.row = row;
		this.column = column;
		this.worksheet = worksheet;
	}

	public CellIndex(String index, WorkSheet worksheet) {
		this.worksheet = worksheet;
		char letter = index.charAt(0);
		column = letter - 'A';
		row = Integer.parseInt(index.substring(1)) - 1;
	}

	@Override
	public boolean equals(Object obj) {
		CellIndex o = (CellIndex) obj;
		return row == o.row && column == o.column;
	}

	@Override
	public int hashCode() {
		return show().hashCode();
	}

	public String show() {
		return ((char) ('A' + (char) column)) + "" + (row + 1);
	}
	public Cell getCell(){
		return worksheet == null? new Cell() : worksheet.lookup(this);
	}
	@Override
	public double evaluate() {
		Cell c = getCell();
		c.calcuate(worksheet);
		return c.value() == null ? 0 : c.value();
	}

	@Override
	public String toLatex() {
		// TODO Auto-generated method stub
		return show();
	}

	@Override
	public List<CellIndex> getReferencedCells() {
		// TODO Auto-generated method stub
		List<CellIndex> ref = new ArrayList<CellIndex>();
		ref.add(this);
		return ref;
	}
}
