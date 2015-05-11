package dataStructures;

import java.util.List;

public class ListStreamReverse implements Stream{
	List<Object> l;
	int cPos;
	boolean reachedEnd = false;

	public ListStreamReverse(List<Object> l) {
		this.l = l;
		cPos = l.size()-1;
	}

	public Object current() {
		if (l.size() > 0)
			return l.get(cPos);
		return null;
	}

	public Object peekNext() {
		if (this.hasNext()) {
			return l.get(cPos - 1);
		}
		return null;
	}

	public Object next() {
		if (this.hasNext()) {
			cPos--;
			return current();
		}
		reachedEnd = true;
		return null;
	}

	public boolean hasEnded() {
		return reachedEnd;
	}

	public boolean hasNext() {
		return cPos > 0;
	}

	public boolean isEnd() {
		return cPos == 0;
	}

	@Override
	public Object peekPrevious() {
		if(cPos+1 < l.size())
			return l.get(cPos+1);
		return null;
	}
}
