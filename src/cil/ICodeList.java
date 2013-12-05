package cil;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Maintains a list of ICodes.
 */
public class ICodeList extends AbstractCollection<ICode> {
	private ICode head;
	private ICode tail;

	private static final boolean doSanityChecks = true;

	public ICodeList() {
		head = null;
		tail = null;
	}

	public ICode getHead() {
		return head;
	}

	public ICode getTail() {
		return tail;
	}

	/**
	 * Append an ICode to this list.
	 * @return 
	 */
	public boolean add(ICode icode) {
		if(doSanityChecks) {
			assert icode.next == null && icode.prev == null;
			assert isNotBroken();
		}
		icode.next = null;
		if (head == null) {
			head = icode;
		} else {
			tail.next = icode;
		}
		icode.prev = tail;
		tail = icode;
		return true;
	}

	/**
	 * Appends list to this list.
	 */
	public void concat(ICodeList list) {
		if(doSanityChecks) {
			assert list != this;
			assert isNotBroken();
			assert list.isNotBroken();
		}
		if (head == null) {
			head = list.head;
			tail = list.tail;
			return;
		}

		tail.next = list.head;

		if (list.head == null) {
			return;
		}

		list.head.prev = tail;
		tail = list.tail;
		list.head = list.tail = null;
	}

	@Override
	public boolean isEmpty() {
		return head == null;
	}

	/**
	 * Add the instruction toPrepend before the instruction
	 * icode
	 */
	public void addBefore(ICode icode, ICode toPrepend) {
		if(doSanityChecks) {
			assert toPrepend.next == null && toPrepend.prev == null;
			assert isNotBroken();
		}

		if (icode == head) {
			head = toPrepend;
		} else {
			icode.prev.next = toPrepend;
		}
		toPrepend.next = icode;
		toPrepend.prev = icode.prev;
		icode.prev = toPrepend;
	}

	/**
	 * Add the instruction toAppend after the instruction
	 * icode
	 */
	public void addAfter(ICode icode, ICode toAppend) {
		if(doSanityChecks) {
			assert toAppend.next == null && toAppend.prev == null;
			assert isNotBroken();
		}
		if (icode == tail) {
			tail = toAppend;
		} else {
			icode.next.prev = toAppend;
		}
		toAppend.prev = icode;
		toAppend.next = icode.next;
		icode.next = toAppend;
	}

	@Override
	public Iterator<ICode> iterator() {
		if(doSanityChecks) {
			assert isNotBroken();
		}

		return new Iterator<ICode>() {
			private ICode myNext = head;
			private ICode removable = null;
			
			@Override
			public boolean hasNext() {
				return myNext != null;
			}

			@Override
			public ICode next() {
				if(myNext == null)
					throw new NoSuchElementException();
				ICode tmp = myNext;
				removable = tmp;
				myNext = myNext.next;
				return tmp;
			}

			@Override
			public void remove() {
				if(doSanityChecks)
					assert isNotBroken();
				if(removable == null) {
					throw new IllegalStateException();
				}
				if(removable.next == null) {
					tail = removable.prev;
				} else {
					removable.next.prev = removable.prev;
				}
				if(removable.prev == null) {
					head = removable.next;
				} else {
					removable.prev.next = removable.next;
				}
				removable.next = removable.prev = null;
				removable = null;
			}
		};
	}

	@Override
	public int size() {
		int tmp = 0;
		ICode t = head;
		while(t != null) {
			tmp++;
			t = t.next;
		}
		return tmp;
	}

	public boolean isNotBroken() {
		if(head == null || tail == null) {
			return head == null && tail == null;
		}
		if(head.prev != null || tail.next != null) {
			return false;
		}
		ICode fast = head, slow = head;
		boolean odd = false;
		while(true) {
			if(fast.next == null) {
				return fast == tail;
			}
			if(fast.next.prev != fast) {
				return false;
			}
			fast = fast.next;
			if(odd) {
				slow = slow.next;
			}
			odd = !odd;
			if(slow == fast) { // LOOP DETECTED!
				return false;
			}
		}
	}
}

