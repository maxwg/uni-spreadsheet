package dataStructures;

import java.util.List;

public interface Stream {

	public Object current();

	public Object peekNext();

	public Object next();

	public Object peekPrevious();
	
	public boolean hasEnded();

	public boolean hasNext();

	public boolean isEnd();
}
