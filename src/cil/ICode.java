package cil;

import cil.visitors.CILVisitor;

/**
 * Abstract class that represents a single CIL statement.
 * To make code transformations (insert/delete/move ICodes) easy and
 * efficient, ICodes are organized as a double-linked list with the prev
 * and next fields in the ICode.
 */
public abstract class ICode {
    public ICode prev;
    public ICode next;

    /**
     * Creates a new ICode
     *  
     */
    protected ICode() {
	    prev = null;
	    next = null;
    }

    /**
     * Abstract method for the visitor pattern
     * 
     * @param visitor
     *            CILVisitor to accept
     */
    public abstract void accept(final CILVisitor visitor);

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public abstract String toString();

    /**
     * Returns the name of the class
     * 
     * @return name of the class
     */
    public final String getClassName() {
        String cn = this.getClass().getName();
        return cn.substring(cn.lastIndexOf('.') + 1, cn.length());
    }
}
