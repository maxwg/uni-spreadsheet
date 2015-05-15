package dataStructures;

import java.util.List;

import expressions.ParseException;

public class ListStream implements Stream{
	List<Object> l;
	int cPos;
	boolean reachedEnd = false;

	public ListStream(List<Object> l) {
		this.l = l;
		cPos = 0;
	}

	public Object current() {
		if (l.size() > 0)
			return l.get(cPos);
		return null;
	}

	public Object peekNext() {
		if (this.hasNext()) {
			return l.get(cPos + 1);
		}
		return null;
	}

	public Object next() {
		if (this.hasNext()) {
			cPos++;
			return current();
		}
		reachedEnd = true;
		return null;
	}

	public boolean hasEnded() {
		return reachedEnd;
	}

	public boolean hasNext() {
		return cPos < l.size() - 1;
	}

	public boolean isEnd() {
		return cPos == l.size() - 1;
	}
	
	@Override
	public Object peekPrevious() {
		if(cPos-1 >= 0 )
			return l.get(cPos-1);
		return null;
	}
	
	@Override
	public void validateCurrent(String val)
			throws ParseException {
		if (!val.equals(this.current())) {
			throw new ParseException("current token: "+this.current()+", expected :"+val);
		}
		this.next();
	}
	
	@Override
	public void validateNext(String val)
			throws ParseException {
		if (!val.equals(this.next())) {
			throw new ParseException("current token: "+this.current()+", expected :"+val+"\nAt: positon "+position());
		}
	}

	@Override
	public int position() {
		// TODO Auto-generated method stub
		return cPos;
	}

	@Override
	public void setPosition(int n) {
		cPos = n;
	}


}
