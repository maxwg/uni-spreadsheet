package dataStructures;

import java.util.List;

public class ListStream {
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
}
