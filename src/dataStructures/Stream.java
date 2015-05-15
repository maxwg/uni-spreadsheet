package dataStructures;

import java.util.List;

import expressions.ParseException;

public interface Stream {

	public Object current();

	public Object peekNext();

	public Object next();

	public Object peekPrevious();
	
	public boolean hasEnded();

	public boolean hasNext();

	public boolean isEnd();
	
	public void validateCurrent(String expected) throws ParseException;
	
	public void validateNext(String expected) throws ParseException;
	
	public int position();
	
	public void setPosition(int n);
}
