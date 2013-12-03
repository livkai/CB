package cil;

import cil.visitors.CILVisitor;

/**
 * CIL representation of an array store
 */
public final class CSTORE extends ICode {
    private Operand base;
    private Operand offset;
    private Operand value;

    /**
     * Creates a new CSTORE
     * 
     * @param base
     *            base address of the array
     * @param off
     *            offset into the array
     * @param val
     *            value to store in the array
     */
    public CSTORE(final Operand base, final Operand off, final Operand val) {
	    this.base = base;
	    offset = off;
	    value = val;
    }

    /**
     * Register an CILVisitor at this ICode (visitor pattern)
     * 
     * @param visitor
     *            CILVisitor to register at this ICode
     */
    public final void accept(final CILVisitor visitor) {
        visitor.visit(this);
        return;
    }

    /**
     * Returns the string representation for this class
     * 
     * @return representation for this class
     */
    public final String toString() {
        return  base + "[" + offset + "] = " + value;
    }

    /**
     * Returns the base.
     */
    public Operand getBaseOperand() {
        return base;
    }

    /**
     * Set the base.
     */
    public void setBaseOperand(Operand b) {
        base = b;
    }

    /**
     * Returns the offset.
     */
    public Operand getOffsetOperand() {
        return offset;
    }

    /**
     * Set the offset.
     */
    public void setOffsetOperand(Operand o) {
        offset = o;
    }

    /**
     * Returns the value.
     */
    public Operand getValueOperand() {
        return value;
    }

    /**
     * Set the value.
     */
    public void setValueOperand(Operand v) {
        value = v;
    }
}
